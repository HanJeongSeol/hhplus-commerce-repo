package kr.hhplus.be.server.infra.order;

import kr.hhplus.be.server.application.order.OrderResult;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderRepositoryImpl implements OrderRepository {

    private final OrderJpaRepository orderJpaRepository;
    private final OrderQueryDslRepository orderQueryDslRepository;

    @Override
    public Order save(Order order) {
        return orderJpaRepository.save(order);
    }

    @Override
    public Optional<Order> findById(Long orderId) {
        return orderJpaRepository.findById(orderId);
    }

    @Override
    public Optional<OrderResult> findByIdWithLines(Long orderId) {
        return orderQueryDslRepository.findByIdWithLines(orderId);
    }

    @Override
    public List<OrderResult> findByUserIdWithLines(Long userId) {
        return orderQueryDslRepository.findByUserIdWithLines(userId);
    }

    @Override
    public Page<OrderResult> findByUserIdWithLines(Long userId, Pageable pageable) {
        return orderQueryDslRepository.findByUserIdWithLines(userId, pageable);
    }
}