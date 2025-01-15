package kr.hhplus.be.server.interfaces.dto.order.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.order.response.OrderResult;
import kr.hhplus.be.server.support.constant.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "주문 응답 DTO")
public class OrderResponse {
    @Schema(description = "주문 정보 응답")
    public record OrderInfoResponse(
            @Schema(description = "주문 ID", example = "1")
            Long orderId,

            @Schema(description = "사용자 ID", example = "1")
            Long userId,

            @Schema(description = "주문 상태")
            OrderStatus status,

            @Schema(description = "총 주문 금액", example = "50000")
            Long totalPrice,

            @Schema(description = "주문 상품 목록")
            List<OrderItemResponse> orderItems,

            @Schema(description = "주문 일시")
            LocalDateTime createdAt
    ) {
        public static OrderInfoResponse from(OrderResult.OrderInfoResult result){
            return new OrderInfoResponse(
                    result.orderId(),
                    result.userId(),
                    result.status(),
                    result.totalPrice(),
                    result.orderItems().stream()
                            .map(OrderItemResponse::from)
                            .toList(),
                    result.createdAt()
            );
        }
    }

    @Schema(description = "주문 상품 정보 응답")
    public record OrderItemResponse(
            @Schema(description = "상품 ID", example = "1")
            Long productId,
            @Schema(description = "상품명", example = "항해 기념품")
            String productName,

            @Schema(description = "주문 수량", example = "2")
            Integer quantity,

            @Schema(description = "상품 가격", example = "25000")
            Long price,

            @Schema(description = "총 금액", example = "50000")
            Long totalPrice
    ) {
        public static OrderItemResponse from(OrderResult.OrderLineResult result){
            return new OrderItemResponse(
                    result.productId(),
                    result.productName(),
                    result.quantity(),
                    result.price(),
                    result.totalPrice()
            );
        }
    }
}
