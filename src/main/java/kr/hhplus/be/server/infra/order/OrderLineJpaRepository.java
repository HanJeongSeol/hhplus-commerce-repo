package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.domain.order.OrderLine;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderLineJpaRepository extends JpaRepository<OrderLine, Long> {
}
