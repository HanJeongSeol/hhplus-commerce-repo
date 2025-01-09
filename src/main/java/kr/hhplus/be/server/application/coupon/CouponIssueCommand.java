package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.interfaces.dto.coupon.CouponIssueRequest;

public record CouponIssueCommand(
        Long userId,
        Long couponId
) {
    public static CouponIssueCommand from(CouponIssueRequest request) {
        return new CouponIssueCommand(
                request.userId(),
                request.couponId()
        );
    }
}
