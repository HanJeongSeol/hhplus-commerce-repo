package kr.hhplus.be.server.domain.payment;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface PaymentRepository {
    Payment save(Payment payment);
    Optional<Payment> findById(Long paymentId);
    Optional<Payment> findByOrderId(Long orderId);
    List<Payment> findByUserId(Long userId);
    Optional<Payment> findByIdWithLock(Long paymentId);
    Page<Payment> findByUserId(Long userId, Pageable pageable);

}
