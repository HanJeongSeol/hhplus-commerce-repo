package kr.hhplus.be.server.domain.order;

import java.util.List;
import java.util.Optional;

public interface OrderRepository {
    Order save(Order order);
    List<OrderLine> saveAll(List<OrderLine> orderLines);
    Optional<Order> findById(Long orderId);

    List<OrderLine> findByOrderLineId(Long orderId);

}
