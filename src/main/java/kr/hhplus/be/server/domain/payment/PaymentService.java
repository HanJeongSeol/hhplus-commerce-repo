package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.constant.PaymentStatus;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class PaymentService {
    private final PaymentRepository paymentRepository;

    /**
     * 결제 생성
     */
    @Transactional
    public Payment createPayment(Long orderId, Long userId, Long paymentPrice) {
        Payment payment = Payment.create(orderId, userId, paymentPrice);

        return paymentRepository.save(payment);
    }

    /**
     * 결제 승인
     */
    @Transactional
    public Payment approvePayment(Long paymentId) {
        Payment payment = paymentRepository.findByIdWithLock(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));

        payment.approve();
        return paymentRepository.save(payment);
    }

    /**
     * 결제 조회
     */
    public Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    /**
     * 주문별 결제 조회
     */
    public Payment getPaymentByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    /**
     * 사용자별 결제 목록 조회
     */
    public List<Payment> getUserPayments(Long userId) {
        return paymentRepository.findByUserId(userId);
    }

    /**
     * 사용자별 결제 목록 페이징 조회
     */
    public Page<Payment> getUserPayments(Long userId, Pageable pageable) {
        return paymentRepository.findByUserId(userId, pageable);
    }
}
