package kr.hhplus.be.server.interfaces.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "주문 상품 요청 DTO")
public record OrderItemRequest(
        @Schema(description = "상품 ID", example = "1")
        Long productId,

        @Schema(description = "주문 수량", example = "2")
        Integer quantity

) {}
