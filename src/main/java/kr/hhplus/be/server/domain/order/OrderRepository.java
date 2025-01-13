package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.application.order.OrderResult;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long orderId);
    Optional<OrderResult> findByIdWithLines(Long orderId);
    List<OrderResult> findByUserIdWithLines(Long userId);
    Page<OrderResult> findByUserIdWithLines(Long userId, Pageable pageable);
}
