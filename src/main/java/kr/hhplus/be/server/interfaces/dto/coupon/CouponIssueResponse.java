package kr.hhplus.be.server.interfaces.dto.coupon;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.support.constant.CouponStatus;

import java.time.LocalDateTime;

@Schema(description = "쿠폰 발급 응답 DTO")
public record CouponIssueResponse(
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
) {}