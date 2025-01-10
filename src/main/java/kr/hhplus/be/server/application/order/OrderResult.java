package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.interfaces.dto.order.OrderItemResponse;
import kr.hhplus.be.server.interfaces.dto.order.OrderResponse;
import kr.hhplus.be.server.support.constant.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResult(
        Long orderId,
        Long userId,
        OrderStatus status,
        LocalDateTime orderDate,
        Long totalAmount,
        List<OrderItemResult> items
) {
    public static OrderResult of(Order order) {
        List<OrderItemResult> items = order.getOrderDetails().stream()
                .map(OrderItemResult::of)
                .toList();

        return new OrderResult(
                order.getOrderId(),
                order.getUserId(),
                order.getStatus(),
                order.getCreatedAt(),
                order.getTotalAmount(),
                items
        );
    }

    public OrderResponse toResponse() {
        List<OrderItemResponse> itemResponses = items.stream()
                .map(OrderItemResult::toResponse)
                .toList();

        return new OrderResponse(
                orderId,
                userId,
                status,
                orderDate,
                totalAmount,
                itemResponses
        );
    }
}