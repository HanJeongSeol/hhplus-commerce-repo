package kr.hhplus.be.server.interfaces.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderLine;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.infra.order.OrderJpaRepository;
import kr.hhplus.be.server.infra.order.OrderLineJpaRepository;
import kr.hhplus.be.server.infra.product.ProductJpaRepository;
import kr.hhplus.be.server.interfaces.dto.order.request.OrderRequest;
import kr.hhplus.be.server.interfaces.dto.payment.request.PaymentRequest;
import kr.hhplus.be.server.support.constant.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class PaymentControllerIntegrationTest {

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
    }

    @Test
    @DisplayName("[POST] /api/v1/payments - 결제 처리 테스트")
    void 사용자아이디와_주문아이디를_전달받아서_결제_처리_성공() throws Exception {
        // given
        PaymentRequest.PaymentProcessingRequest request = new PaymentRequest.PaymentProcessingRequest(
                testOrderId,
                userId,
                null
        );
        String requestJson = objectMapper.writeValueAsString(request);

        // when & then
        mockMvc.perform(post("/api/v1/payments")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.paymentId").exists())
                .andExpect(jsonPath("$.data.orderId").value(testOrderId.intValue()))
                .andExpect(jsonPath("$.data.userId").value(userId.intValue()))
                .andExpect(jsonPath("$.data.status").exists())
                .andExpect(jsonPath("$.data.totalAmount").value(100000))
                .andExpect(jsonPath("$.data.finalAmount").value(100000))
                .andExpect(jsonPath("$.data.remainingPoints").exists());
    }
}
