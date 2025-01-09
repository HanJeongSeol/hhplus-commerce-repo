package kr.hhplus.be.server.application.product;

import kr.hhplus.be.server.interfaces.dto.product.ProductPolularItemResponse;
import kr.hhplus.be.server.support.constant.ProductStatus;

public record ProductPopularItemResult(
        int rank,
        Long productId,
        String productName,
        int salesCount,
        Long price,
        Integer stock,
        ProductStatus status
) {
    public static ProductPopularItemResult of(
            int rank,
            Long productId,
            String productName,
            Long salesCount,
            Long price,
            Integer stock,
            ProductStatus status
    ) {
        return new ProductPopularItemResult(
                rank,
                productId,
                productName,
                salesCount.intValue(),
                price,
                stock,
                status
        );
    }

    public ProductPolularItemResponse toResponse() {
        return new ProductPolularItemResponse(
                rank,
                productId,
                productName,
                salesCount,
                price,
                stock,
                status
        );
    }
}