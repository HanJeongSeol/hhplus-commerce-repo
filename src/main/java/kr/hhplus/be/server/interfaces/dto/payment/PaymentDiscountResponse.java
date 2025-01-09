package kr.hhplus.be.server.interfaces.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

public record PaymentDiscountResponse(
        @Schema(description = "쿠폰 ID", example = "1")
        Long couponId,

        @Schema(description = "쿠폰명", example = "신규 가입 할인 쿠폰")
        String couponName,

        @Schema(description = "할인 금액", example = "5000")
        Long discountAmount,

        @Schema(description = "쿠폰 사용 일시")
        LocalDateTime usedAt
) {
}
