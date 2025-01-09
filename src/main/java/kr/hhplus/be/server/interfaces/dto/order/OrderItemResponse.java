package kr.hhplus.be.server.interfaces.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 상품 아이템 응답 DTO")
public record OrderItemResponse(
        @Schema(description = "상품 ID", example = "1")
        Long productId,

        @Schema(description = "상품명", example = "항해 기념품")
        String productName,

        @Schema(description = "주문 수량", example = "2")
        Integer quantity,

        @Schema(description = "단가", example = "25000")
        Long price,

        @Schema(description = "총 가격", example = "50000")
        Long totalPrice
) {
}
