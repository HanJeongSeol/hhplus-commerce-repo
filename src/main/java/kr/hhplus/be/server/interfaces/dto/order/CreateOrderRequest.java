package kr.hhplus.be.server.interfaces.dto.order;


import io.swagger.v3.oas.annotations.media.Schema;

import java.util.List;


@Schema(description = "주문 생성 요청 DTO")
public record CreateOrderRequest(
        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "주문 상품 목록")
        List<OrderItemRequest> items
) {}