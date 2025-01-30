package kr.hhplus.be.server.interfaces.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.infra.order.OrderJpaRepository;
import kr.hhplus.be.server.infra.order.OrderLineJpaRepository;
import kr.hhplus.be.server.infra.product.ProductJpaRepository;
import kr.hhplus.be.server.interfaces.dto.order.request.OrderRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class OrderControllerIntegrationTest {

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
                .status(kr.hhplus.be.server.support.constant.ProductStatus.ON_SALE)
                .build();
        product = productJpaRepository.save(product);
        testProductId = product.getProductId();
    }

    @Test
    @DisplayName("[POST] /api/v1/orders - 주문 생성 성공 테스트")
    void 사용자아이디와_구매하려는_상품아이디와_상품수량_리스트를_전달받아서_주문_생성() throws Exception {
        // given
        OrderRequest.CreateOrderRequest orderRequest = new OrderRequest.CreateOrderRequest(
                1L,
                List.of(new OrderRequest.OrderItemRequest(testProductId, 2))
        );

        String requestJson = objectMapper.writeValueAsString(orderRequest);

        // when & then
        mockMvc.perform(post("/api/v1/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.orderId").exists())
                .andExpect(jsonPath("$.data.userId").value(1))
                .andExpect(jsonPath("$.data.status").value("PENDING"))
                .andExpect(jsonPath("$.data.totalPrice").value(100000))
                .andExpect(jsonPath("$.data.orderItems").isArray())
                .andExpect(jsonPath("$.data.orderItems[0].productId").value(testProductId.intValue()))
                .andExpect(jsonPath("$.data.orderItems[0].quantity").value(2))
                .andExpect(jsonPath("$.data.orderItems[0].price").value(50000))
                .andExpect(jsonPath("$.data.orderItems[0].totalPrice").value(100000));
    }
}
