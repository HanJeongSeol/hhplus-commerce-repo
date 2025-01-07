package kr.hhplus.be.server.interfaces.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.support.constant.ProductStatus;

public record ProductPolularItemResponse(
        @Schema(description = "순위", example = "1")
        int rank,

        @Schema(description = "상품 ID", example = "1")
        Long productId,

        @Schema(description = "상품 이름", example = "항해 기념품")
        String productName,

        @Schema(description = "판매 수량", example = "150")
        int salesCount,

        @Schema(description = "상품 가격", example = "25000")
        Long price,
        @Schema(description = "재고 수량", example = "100")
        Integer stock,
        @Schema(description = "상품 상태", example = "판매중")
        ProductStatus status
) {
}
