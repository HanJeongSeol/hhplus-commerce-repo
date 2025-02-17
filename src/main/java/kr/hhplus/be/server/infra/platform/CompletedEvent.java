package kr.hhplus.be.server.infra.platform;

import kr.hhplus.be.server.application.payment.response.PaymentResult;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.support.constant.PaymentStatus;

import java.time.LocalDateTime;

public class CompletedEvent{
    public record PaymentCompletedEvent(
            Long paymentId,
            Long orderId,
            Long userId,
            PaymentStatus status,
            Long totalAmount,
            PaymentResult.DiscountInfo discountInfo,
            Long finalAmount,
            LocalDateTime completedAt

    ){
        public static PaymentCompletedEvent of(
                Payment payment,
                Long totalAmount,
                PaymentResult.DiscountInfo discountInfo
        ) {
            return new PaymentCompletedEvent(
                    payment.getPaymentId(),
                    payment.getOrderId(),
                    payment.getUserId(),
                    payment.getStatus(),
                    totalAmount,
                    discountInfo,
                    payment.getPaymentPrice(),
                    payment.getCreatedAt()
            );
        }
    }
}