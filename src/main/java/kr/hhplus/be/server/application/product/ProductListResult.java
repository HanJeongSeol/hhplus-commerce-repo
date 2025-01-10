package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.interfaces.dto.product.ProductResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public record ProductListResult(
        List<ProductResult> products,
        int totalPages,
        long totalElements,
        boolean hasNext
) {
    public static ProductListResult of(Page<Product> productPage) {
        List<ProductResult> products = productPage.getContent().stream()
                .map(ProductResult::of)
                .toList();

        return new ProductListResult(
                products,
                productPage.getTotalPages(),
                productPage.getTotalElements(),
                productPage.hasNext()
        );
    }

    public List<ProductResponse> toResponse() {
        return products.stream()
                .map(ProductResult::toResponse)
                .toList();
    }
}