package kr.hhplus.be.server.domain.payment.dto;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.support.constant.PaymentStatus;

import java.time.LocalDateTime;

public class PaymentInfo {
    public record PaymentCreate(
            Long orderId,
            Long userId,
            Long totalAmount,
            Long discountAmount,
            Long finalAmount
    ) {}
    public record PaymentDetail(
            Long paymentId,
            Long orderId,
            Long userId,
            PaymentStatus status,
            Long paymentPrice,
            LocalDateTime createdAt
    ) {
        public static PaymentDetail from(Payment payment){
            return new PaymentDetail(
                    payment.getPaymentId(),
                    payment.getOrderId(),
                    payment.getUserId(),
                    payment.getStatus(),
                    payment.getPaymentPrice(),
                    payment.getCreatedAt()
            );
        }
    }
}
