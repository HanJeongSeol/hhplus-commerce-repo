package kr.hhplus.be.server.interfaces.dto.payment.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.payment.response.PaymentResult;
import kr.hhplus.be.server.support.constant.PaymentStatus;

import java.time.LocalDateTime;

public class PaymentResponse {
        @Schema(description = "결제 처리 응답 DTO")
        public record PaymentProcessingResponse(
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
        ) {
                public static PaymentProcessingResponse from(PaymentResult.PaymentProcessResult result) {
                        return new PaymentProcessingResponse(
                                result.paymentId(),
                                result.orderId(),
                                result.userId(),
                                result.status(),
                                result.paymentDate(),
                                result.totalAmount(),
                                result.discountInfo() != null ?
                                        PaymentDiscountResponse.from(result.discountInfo()) : null,
                                result.finalAmount(),
                                result.remainingPoints()
                        );
                }
        }

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
                public static PaymentDiscountResponse from(PaymentResult.DiscountInfo discountInfo) {
                        return new PaymentDiscountResponse(
                                discountInfo.couponId(),
                                discountInfo.couponName(),
                                discountInfo.discountAmount(),
                                discountInfo.usedAt()
                        );
                }
        }

}