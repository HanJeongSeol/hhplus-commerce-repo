package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductPopularList;
import kr.hhplus.be.server.domain.product.ProductPopularQueryDto;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
@RequiredArgsConstructor
public class ProductFacade {
    private final ProductService productService;

    /**
     * 인기 상품 목록 조회
     * 최근 3일간의 판매량을 기준으로 상위 5개 상품 조회
     */
    public ProductPopularResult getPopularProducts() {
        List<ProductPopularList> popularProducts = productService.getPopularProducts();

        List<ProductPopularDetailCommand> items = popularProducts.stream()
                .map(product -> ProductPopularDetailCommand.of(
                        Product.builder()
                                .productId(product.productId())
                                .name(product.productName())
                                .price(product.price())
                                .stock(product.stock())
                                .status(product.status())
                                .build(),
                        product.rank(),
                        product.salesCount().intValue()
                ))
                .toList();

        return new ProductPopularResult(LocalDateTime.now(), items);
    }

    public ProductListResult getProducts(Pageable pageable) {
        Page<Product> productPage = productService.getAllProductPage(pageable);
        return ProductListResult.of(productPage);
    }


    public ProductResult getProduct(Long productId) {
        Product product = productService.getProductById(productId);
        return ProductResult.of(product);
    }
}
