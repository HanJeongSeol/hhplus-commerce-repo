package kr.hhplus.be.server.application.payment.request;

import kr.hhplus.be.server.interfaces.dto.payment.request.PaymentRequest;

public class PaymentCommand {
    public record ProcessPayment(
            Long orderId,
            Long userId,
            Long userCouponId
    ) {
        public static ProcessPayment from(PaymentRequest .PaymentProcessingRequest request){
            return new ProcessPayment(
                    request.orderId(),
                    request.userId(),
                    request.userCouponId()
            );
        }
    }

    public record GetPayment(
            Long paymentId
    ) {
        public static GetPayment from(Long paymentId){
            return new GetPayment(paymentId);
        }
    }
}
