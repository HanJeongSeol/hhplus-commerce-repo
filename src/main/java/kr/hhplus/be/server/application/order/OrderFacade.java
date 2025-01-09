package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderDetailProduct;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderService orderService;
    private final ProductService productService;

    @Transactional
    public OrderResult createOrder(OrderCommand command) {
        // 1. 주문 상품 정보 변환 및 검증
        List<OrderDetailProduct> orderProducts = command.orderItemList().stream()
                .map(item -> {
                    Product product = productService.getProductById(item.productId());
                    return OrderDetailProduct.of(
                            product.getProductId(),
                            item.quantity(),
                            product.getPrice()
                    );
                })
                .toList();

        Order order = orderService.createOrder(command.userId(), orderProducts);


        orderProducts.forEach(item ->
                productService.decreaseProductStock(item.productId(), item.quantity())
        );

        return OrderResult.of(order);
    }

    public OrderResult getOrder(Long orderId) {
        Order order = orderService.getOrder(orderId);
        return OrderResult.of(order);
    }


    @Transactional
    public OrderResult cancelOrder(Long orderId) {
        Order order = orderService.cancelOrder(orderId);
        return OrderResult.of(order);
    }

    @Transactional
    public OrderResult completeOrder(Long orderId) {
        Order order = orderService.completeOrder(orderId);
        return OrderResult.of(order);
    }
}
