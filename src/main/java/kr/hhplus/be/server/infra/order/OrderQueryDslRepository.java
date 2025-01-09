package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderQueryDslRepository {
    Optional<Order> findByIdWithDetails(Long orderId);
    List<Order> findByUserIdWithDetails(Long userId);
    Page<Order> findByUserIdWithDetails(Long userId, Pageable pageable);
}
