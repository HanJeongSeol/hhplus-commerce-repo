package kr.hhplus.be.server.domain.order;

public record OrderLineProduct(
        Long productId,
        Integer quantity,
        Long price
) {
    public static OrderLineProduct of(Long productId, Integer quantity, Long price){
        return new OrderLineProduct(productId, quantity, price);
    }
}
