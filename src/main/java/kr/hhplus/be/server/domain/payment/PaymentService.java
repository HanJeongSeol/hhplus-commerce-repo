package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;

    @Transactional
    public Payment createPayment(PaymentCreate paymentCreate) {
        validatePaymentAmount(paymentCreate.paymentAmount());
        validateDuplicatePayment(paymentCreate.orderId());

        Payment payment = Payment.createPayment(
                paymentCreate.orderId(),
                paymentCreate.userId(),
                paymentCreate.paymentAmount()
        );

        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment completePayment(Long paymentId) {
        Payment payment = getPayment(paymentId);
        payment.complete();
        return paymentRepository.save(payment);
    }

    @Transactional
    public Payment canclePayment(Long paymentId) {
        Payment payment = getPayment(paymentId);
        payment.cancel();
        return paymentRepository.save(payment);
    }

    public Payment getPayment(Long paymentId) {
        return paymentRepository.findById(paymentId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }


    public Payment getPaymentByOrder(Long orderId) {
        return paymentRepository.findByOrderId(orderId)
                .orElseThrow(() -> new BusinessException(ErrorCode.PAYMENT_NOT_FOUND));
    }

    private void validateDuplicatePayment(Long orderId) {
        if (paymentRepository.findByOrderId(orderId).isPresent()) {
            throw new BusinessException(ErrorCode.DUPLICATE_PAYMENT);
        }
    }

    private void validatePaymentAmount(Long amount) {
        if (amount <= 0) {
            throw new BusinessException(ErrorCode.INVALID_PAYMENT_AMOUNT);
        }
    }
}
