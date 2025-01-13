package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderLine;
import kr.hhplus.be.server.interfaces.dto.order.OrderResponse;
import kr.hhplus.be.server.support.constant.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public record OrderResult(
        Long orderId,
        Long userId,
        OrderStatus status,
        Long totalPrice,
        LocalDateTime createdAt,
        List<OrderLineResult> orderLines
) {
    public static OrderResult of(Order order, List<OrderLine> orderLines) {
        return new OrderResult(
                order.getOrderId(),
                order.getUserId(),
                order.getStatus(),
                order.getTotalPrice(),
                order.getCreatedAt(),
                orderLines.stream()
                        .map(OrderLineResult::from)
                        .toList()
        );
    }

    public OrderResponse toResponse() {
        return new OrderResponse(
                orderId,
                userId,
                status,
                createdAt,
                totalPrice,
                orderLines.stream()
                        .map(OrderLineResult::toResponse)
                        .toList()
        );
    }
}