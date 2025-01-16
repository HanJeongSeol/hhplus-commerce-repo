package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.dto.OrderInfo;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    /**
     * 주문 생성
     */
    @Transactional
    public OrderInfo.OrderDetail createOrder(Long userId, List<OrderInfo.OrderLineProduct> orderProducts){
        validateOrderProducts(orderProducts);

        // Order 생성
        Order order = Order.create(userId);
        Order saveOrder = orderRepository.save(order);  // orderId 생성 위해 먼저 저장

        List<OrderLine> orderLines = orderProducts.stream()
                .map(product -> OrderLine.createOrderLine(
                        saveOrder.getOrderId(),
                        product.productId(),
                        product.quantity(),
                        product.price()
                )).collect(Collectors.toList());

        List<OrderLine> saveOrderLines = orderRepository.saveAll(orderLines);

        // 총 주문 금액 업데이트
        Long totalPrice = saveOrderLines.stream()
                .mapToLong(OrderLine::getTotalPrice)
                .sum();
        saveOrder.updateTotalPrice(totalPrice);

        // saveOrder 저장
        return OrderInfo.OrderDetail.from(saveOrder, saveOrderLines);
    }

    public Order getOrder(Long orderId){
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));

        return order;
    }

    public List<OrderLine> getOrderLine(Long orderId){
        List<OrderLine> orderLineList = orderRepository.findByOrderLineId(orderId);
        return orderLineList;
    }

    /**
     * 주문 완료
     */
    @Transactional
    public Order completeOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        order.complete();
        return orderRepository.save(order);
    }

    /**
     * 주문 취소
     */
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
        order.cancel();
        return orderRepository.save(order);
    }

    private void validateOrderProducts(List<OrderInfo.OrderLineProduct> orderProducts) {
        if (orderProducts == null || orderProducts.isEmpty()) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_REQUEST);
        }

        orderProducts.forEach(product -> {
            if (product.quantity() <= 0) {
                throw new BusinessException(ErrorCode.INVALID_ORDER_QUANTITY);
            }
            if (product.price() <= 0) {
                throw new BusinessException(ErrorCode.INVALID_PRODUCT_PRICE);
            }
        });
    }

}
