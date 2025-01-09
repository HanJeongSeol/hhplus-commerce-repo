package kr.hhplus.be.server.interfaces.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "결제 처리 요청 DTO")
public record PaymentRequest(
        @Schema(description = "주문 ID", example = "1")
        Long orderId,

        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "사용할 쿠폰 ID", example = "1", required = false)
        Long userCouponId
) {
} 