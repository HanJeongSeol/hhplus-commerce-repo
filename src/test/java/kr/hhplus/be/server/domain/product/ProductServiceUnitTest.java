package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.config.TestUtil;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("상품 서비스 테스트")
public class ProductServiceUnitTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private Product testProduct;
    private List<Product> testProductList;
    @BeforeEach
    void setUp(){
        testProduct = TestUtil.createProduct();
        testProductList = List.of(
                testProduct,
                Product.builder()
                        .productId(2L)
                        .name("항해 녹차")
                        .price(25000L)
                        .stock(100)
                        .build(),
                Product.builder()
                        .productId(3L)
                        .name("항해 기념품2")
                        .price(30000L)
                        .stock(20)
                        .build()
        );
    }

    @Nested
    @DisplayName("상품 목록 조회")
    class GetProducts {

        @Test
        @DisplayName("전체 상품 목록 조회")
        void 사용자가_상품_목록_조회_요청시_모든_상품_목록_리스트를_반환한다() {
            // given
            given(productRepository.findAll()).willReturn(testProductList);

            // when
            List<Product> result = productService.getAllProducts();

            // then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getName()).isEqualTo("항해 기념품");
            assertThat(result.get(1).getName()).isEqualTo("항해 녹차");
            assertThat(result.get(2).getName()).isEqualTo("항해 기념품2");
        }

        @Test
        @DisplayName("전체 상품 목록 페이징")
        void 상품_목록_조회_요칭시_페이징된_상품_목록_리스트를_반환한다(){
            //given
            Page<Product> pageMock = new PageImpl<>(
                    testProductList,
                    PageRequest.of(0, 3),
                    5
            );

            given(productRepository.findAll(any(Pageable.class)))
                    .willReturn(pageMock);

            // when
            Pageable pageable = PageRequest.of(0, 3, Sort.by("productId").descending());
            Page<Product> resultPage = productService.getAllProductPage(pageable);

            // then
            assertThat(resultPage).hasSize(3);
            assertThat(resultPage.getTotalElements()).isEqualTo(5);
            assertThat(resultPage.getContent().get(0).getName()).isEqualTo("항해 기념품");
        }
    }

    @Nested
    @DisplayName("상품 상세 조회")
    class GetProduct {

        @Test
        @DisplayName("상품 상세 조회 성공")
        void 사용자가_특정_상품_조회_요청시_상품_상세_정보를_반환한다() {
            // given
            given(productRepository.findByIdWithLock(testProduct.getProductId()))
                    .willReturn(Optional.of(testProduct));

            // when
            Product result = productService.getProductByIdWithLock(testProduct.getProductId());

            // then
            assertThat(result.getProductId()).isEqualTo(testProduct.getProductId());
            assertThat(result.getName()).isEqualTo(testProduct.getName());
        }

        @Test
        @DisplayName("상품이 존재하지 않을 시 예외")
        void 사용자가_존재하지_않는_상품_조회_요청시_PRODUCT_NOT_FOUND_예외_전달() {
            // given
            given(productRepository.findByIdWithLock(999L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productService.getProductByIdWithLock(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("상품 재고 감소")
    class DecreaseStock {

        @Test
        @DisplayName("재고 감소 성공")
        void 상품_재고_10개_감소_요칭시_90개의_상품_재고가_남는다() {
            // given
            given(productRepository.findByIdWithLock(testProduct.getProductId()))
                    .willReturn(Optional.of(testProduct));
            given(productRepository.save(any(Product.class)))
                    .willReturn(testProduct);

            // when
            productService.decreaseProductStock(testProduct.getProductId(), 10);

            // then
            assertThat(testProduct.getStock()).isEqualTo(90);
            verify(productRepository).save(any(Product.class));
        }

        @Test
        @DisplayName("상품이 존재하지 않을 시 예외")
        void 존재하지_않는_상품_재고_감소_요청시_PRODUCT_NOT_FOUND_예외_전달() {
            // given
            given(productRepository.findByIdWithLock(999L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> productService.decreaseProductStock(999L, 10))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("재고 부족 예외")
        void 재고가_100개인_상품에_101개의_재고감소_요청시_INVALID_ORDER_QUANTITY_예외_발생() {
            // given
            given(productRepository.findByIdWithLock(testProduct.getProductId()))
                    .willReturn(Optional.of(testProduct));

            // when & then
            assertThatThrownBy(() -> productService.decreaseProductStock(testProduct.getProductId(), 101))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.INVALID_ORDER_QUANTITY.getMessage());
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, -1L, -100L, -1000L})
        @DisplayName("잘못된 주문 수량 예외")
        void 주문_수량이_0_혹은_음수인_경우_INVALID_ORDER_QUANTITY_예외_전달() {
            // given
            given(productRepository.findByIdWithLock(testProduct.getProductId()))
                    .willReturn(Optional.of(testProduct));

            // when & then
            assertThatThrownBy(() -> productService.decreaseProductStock(testProduct.getProductId(), 0))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.INVALID_ORDER_QUANTITY.getMessage());
        }
    }
}
