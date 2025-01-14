package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.config.TestUtil;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.constant.ProductStatus;
import kr.hhplus.be.server.support.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("상품 서비스 테스트")
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    private Product testProduct;
    private List<Product> testProducts;

    @BeforeEach
    void setUp() {
        testProduct = TestUtil.createProduct();
        testProducts = List.of(
            testProduct,
            Product.builder()
                .productId(2L)
                .name("항해 후드티")
                .price(35000L)
                .stock(50)
                .status(ProductStatus.ON_SALE)
                .build()
        );
    }

    @Nested
    @DisplayName("상품 목록 조회")
    class GetProducts {

        @Test
        @DisplayName("상품 목록 조회 성공")
        void 상품_목록_조회시_전체_상품_목록을_반환한다() {
            // given
            given(productRepository.findAll())
                .willReturn(testProducts);

            // when
            List<Product> result = productService.getAllProducts();

            // then
            assertThat(result).hasSize(2);
            assertThat(result.get(0).getName()).isEqualTo("항해 기념품");
            assertThat(result.get(1).getName()).isEqualTo("항해 후드티");
        }
    }

    @Nested
    @DisplayName("상품 상세 조회")
    class GetProduct {

        @Test
        @DisplayName("상품 상세 조회 성공")
        void 상품_ID로_조회시_상품_정보를_반환한다() {
            // given
            given(productRepository.findByIdWithLock(testProduct.getProductId()))
                .willReturn(Optional.of(testProduct));

            // when
            Product result = productService.getProductByIdWithLock(testProduct.getProductId());

            // then
            assertThat(result.getProductId()).isEqualTo(testProduct.getProductId());
            assertThat(result.getName()).isEqualTo(testProduct.getName());
            assertThat(result.getPrice()).isEqualTo(testProduct.getPrice());
        }

        @Test
        @DisplayName("존재하지 않는 상품 조회시 예외 발생")
        void 존재하지_않는_상품_ID로_조회시_예외가_발생한다() {
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
        void 재고_감소_요청시_상품_재고가_감소한다() {
            // given
            given(productRepository.findByIdWithLock(testProduct.getProductId()))
                .willReturn(Optional.of(testProduct));
            given(productRepository.save(any(Product.class)))
                .willReturn(testProduct);

            int decreaseQuantity = 10;
            int expectedStock = testProduct.getStock() - decreaseQuantity;

            // when
            productService.decreaseProductStock(testProduct.getProductId(), decreaseQuantity);

            // then
            assertThat(testProduct.getStock()).isEqualTo(expectedStock);
            verify(productRepository).save(testProduct);
        }

        @Test
        @DisplayName("재고보다 많은 수량 요청시 예외 발생")
        void 재고보다_많은_수량_요청시_예외가_발생한다() {
            // given
            given(productRepository.findByIdWithLock(testProduct.getProductId()))
                .willReturn(Optional.of(testProduct));

            // when & then
            assertThatThrownBy(() ->
                productService.decreaseProductStock(testProduct.getProductId(), testProduct.getStock() + 1))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.PRODUCT_OUT_OF_STOCK.getMessage());
        }

        @Test
        @DisplayName("잘못된 수량 요청시 예외 발생")
        void 잘못된_수량_요청시_예외가_발생한다() {
            // given
            given(productRepository.findByIdWithLock(testProduct.getProductId()))
                .willReturn(Optional.of(testProduct));

            // when & then
            assertThatThrownBy(() ->
                productService.decreaseProductStock(testProduct.getProductId(), 0))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INVALID_ORDER_QUANTITY.getMessage());
        }
    }
}