package kr.hhplus.be.server.domain.order;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(Long orderId);
    Optional<Order> findByIdWithDetails(Long orderId);
    List<Order> findByUserIdWithDetails(Long userId);

    Page<Order> findByUserIdWithDetails(Long userId, Pageable pageable);
}
