package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.application.payment.PaymentCommand;

public record PaymentCreate(
        Long orderId,
        Long userId,
        Long paymentAmount
) {
    public static PaymentCreate from(PaymentCommand command){
        return new PaymentCreate(
                command.orderId(),
                command.userId(),
                command.orderAmount()
        );
    }
}
