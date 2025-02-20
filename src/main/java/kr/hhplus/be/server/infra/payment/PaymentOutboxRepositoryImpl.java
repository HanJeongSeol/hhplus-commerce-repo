package kr.hhplus.be.server.infra.payment;

import kr.hhplus.be.server.domain.payment.PaymentOutboxEvent;
import kr.hhplus.be.server.domain.payment.PaymentOutboxEventRepository;
import kr.hhplus.be.server.support.constant.PaymentEventStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class PaymentOutboxRepositoryImpl implements PaymentOutboxEventRepository {
    private final PaymentOutboxEventJpaRepository outboxRepository;

    @Override
    public List<PaymentOutboxEvent> findByStatus(PaymentEventStatus status) {
        return outboxRepository.findByStatus(status);
    }

    @Override
    public void save(PaymentOutboxEvent paymentOutboxEvent) {
        outboxRepository.save(paymentOutboxEvent);
    }

    @Override
    public Optional<PaymentOutboxEvent> findById(Long id) {
        return outboxRepository.findById(id);
    }
}
