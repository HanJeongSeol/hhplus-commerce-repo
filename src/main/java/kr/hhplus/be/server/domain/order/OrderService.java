package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.application.order.OrderResult;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {
    private final OrderRepository orderRepository;

    /**
     * 주문 생성
     */
    @Transactional
    public Order createOrder(Long userId, List<OrderLineProduct> orderProducts){
        validateOrderProducts(orderProducts);

        // Step 1: Order 생성
        Order order = Order.createOrder(userId);

        // Step 2: OrderLine 추가
        orderProducts.forEach(product -> {
            OrderLine orderLine = OrderLine.createOrderLine(
                    product.productId(),
                    product.quantity(),
                    product.price()
            );
            order.addOrderLine(orderLine);
        });

        // Step 4: Order 저장
        return orderRepository.save(order);
    }

    /**
     * 주문 조회
     */
    @Transactional
    public OrderResult getOrder(Long orderId){
        return orderRepository.findByIdWithLines(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    /**
     * 주문 목록 조회
     */
    public List<OrderResult> getUserOrders(Long userId) {
        return orderRepository.findByUserIdWithLines(userId);
    }

    /**
     * 주문 목록 조회 페이징
     */
    @Transactional(readOnly = true)
    public Page<OrderResult> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdWithLines(userId, pageable);
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

    private void validateOrderProducts(List<OrderLineProduct> orderProducts) {
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
