package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.interfaces.dto.product.ProductResponse;
import kr.hhplus.be.server.support.constant.ProductStatus;

import java.time.LocalDateTime;

public record ProductResult(
        Long productId,
        String productName,
        Long price,
        Integer stock,
        ProductStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
    public static ProductResult of(Product product) {
        return new ProductResult(
                product.getProductId(),
                product.getName(),
                product.getPrice(),
                product.getStock(),
                product.getStatus(),
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }

    public ProductResponse toResponse() {
        return new ProductResponse(
                productId,
                productName,
                price,
                stock,
                status,
                createdAt,
                updatedAt
        );
    }
}