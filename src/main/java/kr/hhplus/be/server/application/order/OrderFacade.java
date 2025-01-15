package kr.hhplus.be.server.application.order;

import kr.hhplus.be.server.application.order.request.OrderCommand;
import kr.hhplus.be.server.application.order.response.OrderResult;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.order.dto.OrderInfo;
import kr.hhplus.be.server.domain.product.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderFacade {
    private final OrderService orderService;
    private final ProductService productService;

    public OrderResult.OrderInfoResult createOrder(OrderCommand.CreateOrder command) {
        List<OrderInfo.OrderLineProduct> orderProducts = command.items().stream()
                .map(item -> {
                    var product = productService.getProductById(item.productId());
                    return new OrderInfo.OrderLineProduct(
                            item.productId(),
                            item.quantity(),
                            product.price()
                    );
                }).toList();

        OrderInfo.OrderDetail orderDetail = orderService.createOrder(command.userId(), orderProducts);

        // 상품 정보를 포함한 응답 생성
        return enrichOrderResult(orderDetail);
    }

    private OrderResult.OrderInfoResult enrichOrderResult(OrderInfo.OrderDetail orderDetail) {
        List<OrderResult.OrderLineResult> enrichedOrderLines = orderDetail.orderLines().stream()
                .map(line -> {
                    var product = productService.getProductById(line.productId());
                    return OrderResult.OrderLineResult.withProduct(
                            line,
                            product.productName(),
                            product.price()
                    );
                })
                .toList();

        return new OrderResult.OrderInfoResult(
                orderDetail.orderId(),
                orderDetail.userId(),
                orderDetail.status(),
                orderDetail.totalPrice(),
                enrichedOrderLines,
                orderDetail.createdAt()
        );
    }
}
