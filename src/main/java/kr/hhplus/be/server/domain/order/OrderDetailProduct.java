package kr.hhplus.be.server.domain.order;

public record OrderDetailProduct (
        Long productId,
        Integer quantity,
        Long price
){
    public static OrderDetailProduct of(Long productId, Integer quantity, Long price){
        return new OrderDetailProduct(productId, quantity, price);
    }
}
