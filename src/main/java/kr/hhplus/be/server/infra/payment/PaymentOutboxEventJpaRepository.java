package kr.hhplus.be.server.infra.payment;

import kr.hhplus.be.server.domain.payment.PaymentOutboxEvent;
import kr.hhplus.be.server.support.constant.PaymentEventStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PaymentOutboxEventJpaRepository extends JpaRepository<PaymentOutboxEvent, Long> {
    List<PaymentOutboxEvent> findByStatus(PaymentEventStatus status);
}
