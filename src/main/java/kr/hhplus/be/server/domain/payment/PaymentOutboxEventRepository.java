package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.support.constant.PaymentEventStatus;

import java.util.List;
import java.util.Optional;

public interface PaymentOutboxEventRepository {
    List<PaymentOutboxEvent> findByStatus(PaymentEventStatus status);

    void save(PaymentOutboxEvent paymentOutboxEvent);

    Optional<PaymentOutboxEvent> findById(Long id);
}
