package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.application.order.OrderResult;
import kr.hhplus.be.server.domain.order.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface OrderQueryDslRepository {

    Optional<OrderResult> findByIdWithLines(Long orderId);

    List<OrderResult> findByUserIdWithLines(Long userId);

    Page<OrderResult> findByUserIdWithLines(Long userId, Pageable pageable);
}
