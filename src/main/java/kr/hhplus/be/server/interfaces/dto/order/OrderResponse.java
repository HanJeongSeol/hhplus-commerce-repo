package kr.hhplus.be.server.interfaces.dto.order;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.support.constant.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "주문 응답 DTO")
public record OrderResponse(
        @Schema(description = "주문 ID", example = "1")
        Long orderId,

        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "주문 상태", example = "PENDING")
        OrderStatus status,

        @Schema(description = "주문 일시")
        LocalDateTime orderDate,

        @Schema(description = "총 주문 금액", example = "70000")
        Long totalAmount,

        @Schema(description = "주문 상품 목록")
        List<OrderItemResponse> items
) { }