package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.interfaces.dto.coupon.CouponIssueResponse;

public record CouponIssueResult(
        UserCoupon userCoupon,
        Coupon coupon
) {
    public CouponIssueResponse toResponse() {
        return new CouponIssueResponse(
                userCoupon.getUserCouponId(),
                coupon.getName(),
                coupon.getDiscountAmount(),
                userCoupon.getStatus(),
                userCoupon.getCreatedAt(),
                coupon.getExpiredAt()
        );
    }
}

