package kr.hhplus.be.server.domain.order.dto;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderLine;
import kr.hhplus.be.server.support.constant.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class OrderInfo {
    public record OrderDetail(
            Long orderId,
            Long userId,
            OrderStatus status,
            Long totalPrice,
            List<OrderLineInfo> orderLines,
            LocalDateTime createdAt
    ) {
        public static OrderDetail from(Order order, List<OrderLine> orderLines) {
            return new OrderDetail(
                    order.getOrderId(),
                    order.getUserId(),
                    order.getStatus(),
                    order.getTotalPrice(),
                    orderLines.stream()
                            .map(OrderLineInfo::from)
                            .toList(),
                    order.getCreatedAt()
            );
        }
    }

    public record OrderLineInfo(
            Long orderLineId,
            Long productId,
            Integer quantity,
            Long totalPrice
    ) {
        public static OrderLineInfo from(OrderLine orderLine) {
            return new OrderLineInfo(
                    orderLine.getOrderLineId(),
                    orderLine.getProductId(),
                    orderLine.getQuantity(),
                    orderLine.getTotalPrice()
            );
        }
    }

    public record OrderLineProduct(
            Long productId,
            Integer quantity,
            Long price
    ) {
        public static OrderLineProduct from(Long productId, Integer quantity, Long price){
            return new OrderLineProduct(productId, quantity, price);
        }
    }

}
