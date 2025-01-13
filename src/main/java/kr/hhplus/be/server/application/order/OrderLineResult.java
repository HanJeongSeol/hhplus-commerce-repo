package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderLine;
import kr.hhplus.be.server.interfaces.dto.order.OrderItemResponse;

public record OrderLineResult(
        Long orderLineId,
        Long orderId,
        Long productId,
        Integer quantity,
        Long totalPrice
) {
    public static OrderLineResult from(OrderLine orderLine) {
        return new OrderLineResult(
                orderLine.getOrderLineId(),
                orderLine.getOrderId(),
                orderLine.getProductId(),
                orderLine.getQuantity(),
                orderLine.getTotalPrice()
        );
    }

    public OrderItemResponse toResponse() {
        return new OrderItemResponse(
                productId,
                null, // 상품명은 별도 조회 필요
                quantity,
                null, // 단가는 별도 조회 필요
                totalPrice
        );
    }
}