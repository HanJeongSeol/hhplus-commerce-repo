package kr.hhplus.be.server.domain.product;

import kr.hhplus.be.server.support.constant.ProductStatus;

public record ProductPopularQueryDto(
        Long productId,
        String productName,
        Long salesCount,
        Long price,
        Integer stock,
        ProductStatus status
) {
}
