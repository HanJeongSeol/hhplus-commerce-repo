package kr.hhplus.be.server.interfaces.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.infra.order.OrderJpaRepository;
import kr.hhplus.be.server.infra.order.OrderLineJpaRepository;
import kr.hhplus.be.server.infra.product.ProductJpaRepository;
import kr.hhplus.be.server.interfaces.dto.product.request.ProductRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
@SpringBootTest
@AutoConfigureMockMvc
public class ProductControllerIntegrationTest {

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

    @BeforeEach
    void setUp(){
        // 데이터베이스 초기화
        productJpaRepository.deleteAll();
        orderJpaRepository.deleteAll();
        orderLineJpaRepository.deleteAll();

        // 테스트 상품 생성 -> 20개 생성 후 리스트 확인
        for (int i = 1; i <= 20; i++) {
            Product product = Product.builder()
                    .name("상품" + i)
                    .price(10000L * i)
                    .stock(100 + i)
                    .status(kr.hhplus.be.server.support.constant.ProductStatus.ON_SALE)
                    .build();
            productJpaRepository.save(product);
        }
    }

    @Test
    @DisplayName("[GET] /api/v1/products - 페이징 처리된 상품 목록 조회 테스트")
    void 페이징_처리된_상품_목록_조회_성공() throws Exception{
        // given
        ProductRequest.ProductInfoRequest request = new ProductRequest.ProductInfoRequest(0, 10);

        // when & then
        mockMvc.perform(get("/api/v1/products")
                        .param("page", String.valueOf(request.page()))
                        .param("size", String.valueOf(request.size())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content.length()").value(10))
                .andExpect(jsonPath("$.data.content[0].productId").exists())
                .andExpect(jsonPath("$.data.content[0].name").exists());
    }

    @Test
    @DisplayName("[GET] /api/v1/products/{productId} - 상품 상세 조회 테스트")
    void 상품_상세_조회_성공() throws Exception {
        // given
        Product existingProduct = productJpaRepository.findAll(PageRequest.of(0, 1)).getContent().get(0);
        Long productId = existingProduct.getProductId();

        // when & then
        mockMvc.perform(get("/api/v1/products/{productId}", productId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.productId").value(productId.intValue()))
                .andExpect(jsonPath("$.data.name").value(existingProduct.getName()))
                .andExpect(jsonPath("$.data.price").value(existingProduct.getPrice()))
                .andExpect(jsonPath("$.data.stock").value(existingProduct.getStock()));
    }
}
