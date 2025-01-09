package kr.hhplus.be.server.domain.order;

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
     * Facade로부터 전달받은 OrderDetailProduct 목록으로 주문 생성
     */
    @Transactional
    public Order createOrder(Long userId, List<OrderDetailProduct> orderProducts) {
        validateOrderProducts(orderProducts);

        final Order savedOrder = orderRepository.save(Order.createOrder(userId));

        orderProducts.forEach(product -> {
            OrderDetail orderDetail = OrderDetail.createOrderDetail(
                    product.productId(),
                    product.quantity(),
                    product.price()
            );
            savedOrder.addOrderDetail(orderDetail);
        });

        Long sum = savedOrder.getTotalAmount();
        savedOrder.setTotalAmount(sum);

        return orderRepository.save(savedOrder);
    }

    // 주문 조회
    @Transactional
    public Order getOrder(Long orderId) {
        return orderRepository.findByIdWithDetails(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.ORDER_NOT_FOUND));
    }

    // 주문 목록 조회
    @Transactional
    public List<Order> getUserOrders(Long userId) {
        return orderRepository.findByUserIdWithDetails(userId);
    }

    // 주문 목록 조회 페이징
    @Transactional
    public Page<Order> getUserOrders(Long userId, Pageable pageable) {
        return orderRepository.findByUserIdWithDetails(userId, pageable);
    }

    // 주문 완료
    @Transactional
    public Order completeOrder(Long orderId) {
        Order order = getOrder(orderId);
        order.complete();
        return orderRepository.save(order);
    }

    // 주문 취소
    @Transactional
    public Order cancelOrder(Long orderId) {
        Order order = getOrder(orderId);
        order.cancel();
        return orderRepository.save(order);
    }

    private void validateOrderProducts(List<OrderDetailProduct> orderProducts) {
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
