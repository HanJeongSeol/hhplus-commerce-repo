package kr.hhplus.be.server.interfaces.dto.coupon;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "쿠폰 발급 요청 DTO")
public record CouponIssueRequest(
        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "쿠폰 ID", example = "1")
        Long couponId
) {}