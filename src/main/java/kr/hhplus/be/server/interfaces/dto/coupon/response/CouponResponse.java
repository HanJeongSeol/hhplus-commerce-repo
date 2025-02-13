package kr.hhplus.be.server.interfaces.dto.coupon.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.coupon.response.CouponResult;
import kr.hhplus.be.server.support.constant.CouponStatus;

import java.time.LocalDateTime;

@Schema(description = "쿠폰 발급 응답 DTO")
public class CouponResponse {
    public record IssueResponse(
            @Schema(description = "사용자 쿠폰 ID", example = "1")
            Long userCouponId,

            @Schema(description = "쿠폰 이름", example = "신규 가입 할인 쿠폰")
            String couponName,

            @Schema(description = "할인 금액", example = "5000")
            Long discountAmount,

            @Schema(description = "쿠폰 상태", example = "미사용")
            CouponStatus status,

            @Schema(description = "발급 일자", example = "2025-01-01T10:30:00")
            LocalDateTime issueDate,

            @Schema(description = "만료 일자", example = "2025-01-02T23:59:59")
            LocalDateTime expiredAt
    ) {
        public static IssueResponse toResponse(CouponResult.IssueResult issueResult){
            return new IssueResponse(
                    issueResult.userCouponId(),
                    issueResult.couponName(),
                    issueResult.discountPrice(),
                    issueResult.status(),
                    issueResult.issuedAt(),
                    issueResult.expiredAt()
            );
        }
    }

    public record UserCouponResponse(
            @Schema(description = "사용자 쿠폰 ID", example = "1")
            Long userCouponId,
            @Schema(description = "쿠폰 이름", example = "신규 가입 할인 쿠폰")
            String couponName,
            @Schema(description = "할인 금액", example = "5000")
            Long discountPrice,
            @Schema(description = "쿠폰 상태", example = "미사용")
            CouponStatus status,
            @Schema(description = "발급 일자", example = "2025-01-01T10:30:00")
            LocalDateTime issuedAt,
            @Schema(description = "만료 일자", example = "2025-01-02T23:59:59")
            LocalDateTime expiredAt,
            @Schema(description = "사용 일자", example = "2024-12-25T14:20:00")
            LocalDateTime usedAt
    ) {
        public static UserCouponResponse toResponse(CouponResult.UserCouponResult result){
            return new UserCouponResponse(
                    result.userCouponId(),
                    result.couponName(),
                    result.discountPrice(),
                    result.status(),
                    result.issuedAt(),
                    result.expiredAt(),
                    result.usedAt()
            );
        }
    }

    /**
     * 레디스 쿠폰 발급 요청
     */
    public record IssueRequestResponse(
            String message
    ) {
        public static IssueRequestResponse toResponse(CouponResult.IssueRequestResult result) {
            return new IssueRequestResponse(result.message());
        }
    }
}
