package kr.hhplus.be.server.application.order.response;

import kr.hhplus.be.server.domain.order.dto.OrderInfo;
import kr.hhplus.be.server.support.constant.OrderStatus;

import java.time.LocalDateTime;
import java.util.List;

public class OrderResult {
    public record OrderInfoResult(
            Long orderId,
            Long userId,
            OrderStatus status,
            Long totalPrice,
            List<OrderLineResult> orderItems,
            LocalDateTime createdAt
    ) {
        public static OrderInfoResult from(OrderInfo.OrderDetail orderInfo) {
            return new OrderInfoResult(
                    orderInfo.orderId(),
                    orderInfo.userId(),
                    orderInfo.status(),
                    orderInfo.totalPrice(),
                    orderInfo.orderLines().stream()
                            .map(OrderLineResult::from)
                            .toList(),
                    orderInfo.createdAt()
            );
        }
    }

    public record OrderLineResult(
            Long orderLineId,
            Long productId,
            String productName,  // Facade에서 Product 정보 조회 후 채워줌
            Integer quantity,
            Long price,         // Facade에서 Product 정보 조회 후 채워줌
            Long totalPrice
    ) {
        public static OrderLineResult from(OrderInfo.OrderLineInfo line) {
            return new OrderLineResult(
                    line.orderLineId(),
                    line.productId(),
                    null,           // Facade에서 채워질 값
                    line.quantity(),
                    null,           // Facade에서 채워질 값
                    line.totalPrice()
            );
        }

        // Facade에서 Product 정보를 포함한 완성된 OrderLineResult 생성
        public static OrderLineResult withProduct(
                OrderInfo.OrderLineInfo line,
                String productName,
                Long price
        ) {
            return new OrderLineResult(
                    line.orderLineId(),
                    line.productId(),
                    productName,
                    line.quantity(),
                    price,
                    line.totalPrice()
            );
        }
    }
}
