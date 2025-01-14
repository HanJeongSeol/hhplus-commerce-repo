package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.support.constant.ProductStatus;

public record ProductPopularList(
        Integer rank,
        Long productId,
        String productName,
        Long salesCount,
        Long price,
        Integer stock,
        ProductStatus status
) {
    public static ProductPopularList from(ProductPopularQueryDto queryDto, int rank){
        return new ProductPopularList(
                rank,
                queryDto.productId(),
                queryDto.productName(),
                queryDto.salesCount(),
                queryDto.price(),
                queryDto.stock(),
                queryDto.status()
        );
    }
}
