package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.config.TestUtil;
import kr.hhplus.be.server.domain.product.dto.ProductInfo;
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
        void 상품_목록_조회시_전체_상품_목록을_반환() {
            // given
            Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
            Page<Product> productPage = new PageImpl<>(testProducts, pageable, testProducts.size());
            given(productRepository.findAll(any(Pageable.class)))
                    .willReturn(productPage);

            // when
            Page<ProductInfo.ProductDetail> result = productService.getAllProductPage(0, 10);

            // then
            assertThat(result.getContent()).hasSize(2);
            assertThat(result.getContent().get(0).productName()).isEqualTo("항해 기념품");
            assertThat(result.getContent().get(1).productName()).isEqualTo("항해 후드티");
        }

        @Test
        @DisplayName("페이징된 상품 목록이 비어있을 경우 예외 발생")
        void 페이징된_상품_목록이_비어있을경우_PRODUCT_NOT_FOUND_예외_발생() {
            // given
            Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
            Page<Product> emptyPage = new PageImpl<>(List.of(), pageable, 0);
            given(productRepository.findAll(any(Pageable.class)))
                    .willReturn(emptyPage);

            // when & then
            assertThatThrownBy(() -> productService.getAllProductPage(0, 10))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.PRODUCT_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("상품 상세 조회")
    class GetProduct {

        @Test
        @DisplayName("상품 상세 조회 성공")
        void 상품_ID로_조회시_상품_정보를_반환() {
            // given
            given(productRepository.findById(testProduct.getProductId()))
                    .willReturn(Optional.of(testProduct));

            // when
            ProductInfo.ProductDetail result = productService.getProductByIdWithLock(testProduct.getProductId());

            // then
            assertThat(result.productId()).isEqualTo(testProduct.getProductId());
            assertThat(result.productName()).isEqualTo(testProduct.getName());
            assertThat(result.price()).isEqualTo(testProduct.getPrice());
        }

        @Test
        @DisplayName("존재하지 않는 상품 조회시 예외 발생")
        void 존재하지_않는_상품_ID로_조회시_예외가_발생한다() {
            // given
            given(productRepository.findById(999L))
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
        void 재고보다_많은_수량_요청시_PRODUCT_OUT_OF_STOCK_예외_발생() {
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
        void 잘못된_수량_요청시_INVALID_ORDER_QUANTITY_예외_발생() {
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