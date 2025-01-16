package kr.hhplus.be.server.application.payment.response;

import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.support.constant.PaymentStatus;

import java.time.LocalDateTime;

public class PaymentResult {
    public record PaymentProcessResult(
            Long paymentId,
            Long orderId,
            Long userId,
            PaymentStatus status,
            LocalDateTime paymentDate,
            Long totalAmount,
            DiscountInfo discountInfo,
            Long finalAmount,
            Long remainingPoints
    ) {
        public static PaymentProcessResult of(
                Payment payment,
                Long totalAmount,
                DiscountInfo discountInfo,
                Long finalAmount,
                Long remainingPoints
        ) {
            return new PaymentProcessResult(
                    payment.getPaymentId(),
                    payment.getOrderId(),
                    payment.getUserId(),
                    payment.getStatus(),
                    payment.getCreatedAt(),
                    totalAmount,
                    discountInfo,
                    finalAmount,
                    remainingPoints
            );
        }
    }

    public record PaymentInfoResult(
            Long paymentId,
            Long orderId,
            Long userId,
            String userName,
            PaymentStatus status,
            Long paymentPrice,
            LocalDateTime createdAt
    ) {
        public static PaymentInfoResult of(Payment payment, User user) {
            return new PaymentInfoResult(
                    payment.getPaymentId(),
                    payment.getOrderId(),
                    payment.getUserId(),
                    user.getName(),
                    payment.getStatus(),
                    payment.getPaymentPrice(),
                    payment.getCreatedAt()
            );
        }
    }

    public record DiscountInfo(
            Long couponId,
            String couponName,
            Long discountAmount,
            LocalDateTime usedAt
    ) {}
}
