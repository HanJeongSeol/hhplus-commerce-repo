package kr.hhplus.be.server.application.order;

import java.util.List;

public record OrderCommand(
        Long userId,
        List<OrderItem> orderItemList
) {
    public record OrderItem(
            Long productId,
            Integer quantity
    ){}
}
