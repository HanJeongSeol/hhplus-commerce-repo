package kr.hhplus.be.server.application.order.request;

import kr.hhplus.be.server.interfaces.dto.order.request.OrderRequest;

import java.util.List;

public class OrderCommand {
    public record CreateOrder(
            Long userId,
            List<OrderItem> items
    ) {
        public record OrderItem(
                Long productId,
                Integer quantity
        ) {}

        public static CreateOrder from(OrderRequest.CreateOrderRequest request) {
            List<OrderItem> items = request.items().stream()
                    .map(item -> new OrderItem(item.productId(), item.quantity()))
                    .toList();
            return new CreateOrder(request.userId(), items);
        }
    }

    public record GetOrderList(
            Long userId,
            int page,
            int size
    ) {
        public static GetOrderList from(Long userId, OrderRequest.OrderListRequest request) {
            return new GetOrderList(userId, request.page(), request.size());
        }
    }
}
