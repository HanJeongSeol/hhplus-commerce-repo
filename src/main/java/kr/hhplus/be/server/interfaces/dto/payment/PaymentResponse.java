package kr.hhplus.be.server.interfaces.dto.payment;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.support.constant.PaymentStatus;

import java.time.LocalDateTime;

@Schema(description = "결제 처리 응답 DTO")
public record PaymentResponse(
        @Schema(description = "결제 ID", example = "1")
        Long paymentId,

        @Schema(description = "주문 ID", example = "1")
        Long orderId,

        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "결제 상태", example = "COMPLETED")
        PaymentStatus status,

        @Schema(description = "결제 일시")
        LocalDateTime paymentDate,

        @Schema(description = "주문 총 금액", example = "50000")
        Long totalAmount,

        @Schema(description = "할인 정보")
        PaymentDiscountResponse discountInfo,

        @Schema(description = "최종 결제 금액", example = "45000")
        Long finalAmount,

        @Schema(description = "잔여 포인트", example = "105000")
        Long remainingPoints
) {}