package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderLine;

import java.util.List;

public record OrderCommand(
        Long userId,
        List<OrderLineCommand> orderLines
) {
    public record OrderLineCommand(
            Long productId,
            Integer quantity
    ) {
        public OrderLine toEntity(Long price) {
            return OrderLine.createOrderLine(
                    productId,
                    quantity,
                    price
            );
        }
    }

    public Order toEntity() {
        return Order.createOrder(userId);
    }
}