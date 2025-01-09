package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.interfaces.dto.payment.PaymentRequest;

public record PaymentCommand(
        Long orderId,
        Long userId,
        Long couponId,
        Long orderAmount
) {
    public static PaymentCommand from(PaymentRequest request, Long orderAmount){
        return new PaymentCommand(
                request.orderId(),
                request.userId(),
                request.userCouponId(),
                orderAmount
        );
    }
}
