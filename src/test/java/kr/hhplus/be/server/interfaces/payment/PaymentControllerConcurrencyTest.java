package kr.hhplus.be.server.interfaces.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderLine;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.infra.order.OrderJpaRepository;
import kr.hhplus.be.server.infra.order.OrderLineJpaRepository;
import kr.hhplus.be.server.infra.product.ProductJpaRepository;
import kr.hhplus.be.server.interfaces.dto.payment.request.PaymentRequest;
import kr.hhplus.be.server.support.constant.PaymentStatus;
import kr.hhplus.be.server.support.constant.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerConcurrencyTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ProductJpaRepository productJpaRepository;

    @Autowired
    private OrderJpaRepository orderJpaRepository;

    @Autowired
    private OrderLineJpaRepository orderLineJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private PointService pointService;

    private Long testProductId;
    private Long testOrderId;
    private final Long userId = 1L;


    @BeforeEach
    void setUp(){
        // 데이터베이스 초기화
        orderLineJpaRepository.deleteAll();
        orderJpaRepository.deleteAll();
        productJpaRepository.deleteAll();

        // 테스트 상품 생성
        Product product = Product.builder()
                .name("테스트 상품")
                .price(50000L)
                .stock(100)
                .status(ProductStatus.ON_SALE)
                .build();
        product = productJpaRepository.save(product);
        testProductId = product.getProductId();

        // 주문 생성
        Order order = Order.create(userId);
        order = orderJpaRepository.save(order);
        testOrderId = order.getOrderId();

        // 주문 항목 생성
        OrderLine orderLine = OrderLine.createOrderLine(order.getOrderId(), testProductId, 2, product.getPrice());
        orderLine = orderLineJpaRepository.save(orderLine);

        // 주문의 총 금액 업데이트
        order.updateTotalPrice(orderLine.getTotalPrice());
        orderJpaRepository.save(order);

        // 테스트를 위한 포인트 충전
        pointService.chargePoint(userId, 1_000_000L);
    }

    @Test
    @DisplayName("[POST] /api/v1/payments - 동시성 결제 요청 테스트")
    void concurrentProcessPaymentTest() throws Exception {
        // given
        PaymentRequest.PaymentProcessingRequest request = new PaymentRequest.PaymentProcessingRequest(
                testOrderId,
                userId,
                null  // 쿠폰 사용 없음
        );
        String requestJson = objectMapper.writeValueAsString(request);

        int threadCount = 10;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);

        List<Future<Integer>> futures = new ArrayList<>();

        // 여러 스레드가 동시에 결제 요청 보내도록
        for (int i = 0; i < threadCount; i++) {
            futures.add(executor.submit(() -> {
                latch.await();
                MvcResult result = mockMvc.perform(post("/api/v1/payments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                        .andReturn();
                return result.getResponse().getStatus();
            }));
        }

        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        int successCount = 0;
        int failureCount = 0;
        for (Future<Integer> future : futures) {
            int status = future.get();
            if (status == 200) {
                successCount++;
            } else {
                failureCount++;
            }
        }
        assertThat(successCount).isEqualTo(threadCount);
    }

    @Test
    @DisplayName("[Concurrent] 결제 및 상품 조회 동시 요청 테스트")
    void concurrentPaymentAndProductRetrievalTest() throws Exception {
        int paymentThreadCount = 1;
        int retrievalThreadCount = 5;
        int totalThreads = paymentThreadCount + retrievalThreadCount;
        ExecutorService executor = Executors.newFixedThreadPool(totalThreads);
        CountDownLatch latch = new CountDownLatch(1);

        // 결제 요청 DTO 및 JSON 준비
        PaymentRequest.PaymentProcessingRequest paymentRequest = new PaymentRequest.PaymentProcessingRequest(
                testOrderId,
                userId,
                null  // 쿠폰 미사용
        );
        String paymentRequestJson = objectMapper.writeValueAsString(paymentRequest);

        List<Future<Integer>> paymentFutures = new ArrayList<>();
        List<Future<Integer>> retrievalFutures = new ArrayList<>();

        // 결제 요청 스레드 제출
        for (int i = 0; i < paymentThreadCount; i++) {
            paymentFutures.add(executor.submit(() -> {
                latch.await();  // 모든 스레드가 동시에 시작하도록 대기
                MvcResult result = mockMvc.perform(post("/api/v1/payments")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(paymentRequestJson))
                        .andReturn();
                return result.getResponse().getStatus();
            }));
        }

        // 상품 조회 요청 스레드 제출
        for (int i = 0; i < retrievalThreadCount; i++) {
            retrievalFutures.add(executor.submit(() -> {
                latch.await();
                MvcResult result = mockMvc.perform(get("/api/v1/products/{productId}", testProductId))
                        .andReturn();
                return result.getResponse().getStatus();
            }));
        }

        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        // 결제 요청 결과
        int successCount = 0;
        for (Future<Integer> future : paymentFutures) {
            int status = future.get();
            if (status == 200) successCount++;
        }

        // 상품 조회 요청 결과
        for (Future<Integer> future : retrievalFutures) {
            int status = future.get();
            if(status != 200) {
                fail("상품 조회 요청 실패");
            }
        }

        // when & then
        mockMvc.perform(get("/api/v1/products/{productId}", testProductId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.stock").value(98));
    }
}
