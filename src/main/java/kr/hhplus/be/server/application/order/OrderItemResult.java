package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.OrderDetail;
import kr.hhplus.be.server.interfaces.dto.order.OrderItemResponse;

public record OrderItemResult(
        Long productId,
        String productName,
        Integer quantity,
        Long price,
        Long totalPrice
) {
    public static OrderItemResult of(OrderDetail detail) {
        return new OrderItemResult(
                detail.getProductId(),
                detail.getProduct().getName(),
                detail.getStock(),
                detail.getProduct().getPrice(),
                detail.getTotalAmount()
        );
    }

    public OrderItemResponse toResponse() {
        return new OrderItemResponse(
                productId,
                productName,
                quantity,
                price,
                totalPrice
        );
    }
}