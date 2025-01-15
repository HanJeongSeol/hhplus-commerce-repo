package kr.hhplus.be.server.interfaces.dto.coupon.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "쿠폰 요청 DTO")
public class CouponRequest {
    @Schema(description = "쿠폰 발급 요청")
    public record IssueRequest(
            @Schema(description = "사용자 ID", example = "1")
            Long userId,
            @Schema(description = "쿠폰 ID", example = "1")
            Long couponId
    ) {}

    @Schema(description = "사용자 쿠폰 목록 조회 요청")
    public record UserCouponInfoRequest(
            @Schema(description = "사용자 ID", example = "1")
            Long userId
    ) { }
}
