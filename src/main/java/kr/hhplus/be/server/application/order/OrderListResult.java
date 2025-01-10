package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.interfaces.dto.order.OrderResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public record OrderListResult(
        List<OrderResult> orders,
        int totalPages,
        long totalElements,
        boolean hasNext
) {
    public static OrderListResult of(Page<Order> orderPage) {
        List<OrderResult> orders = orderPage.getContent().stream()
                .map(OrderResult::of)
                .toList();

        return new OrderListResult(
                orders,
                orderPage.getTotalPages(),
                orderPage.getTotalElements(),
                orderPage.hasNext()
        );
    }

    public List<OrderResponse> toResponse() {
        return orders.stream()
                .map(OrderResult::toResponse)
                .toList();
    }
}