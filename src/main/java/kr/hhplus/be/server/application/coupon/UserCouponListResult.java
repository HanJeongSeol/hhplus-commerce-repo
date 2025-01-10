package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.interfaces.dto.coupon.UserCouponResponse;

import java.util.List;
import java.util.stream.Collectors;

public record UserCouponListResult(
        List<UserCouponDetail> coupons
) {
    public UserCouponResponse toResponse() {
        List<UserCouponResponse.CouponItem> couponItems = coupons.stream()
                .map(detail -> new UserCouponResponse.CouponItem(
                        detail.userCoupon().getUserCouponId(),
                        detail.coupon().getName(),
                        detail.coupon().getDiscountAmount(),
                        detail.userCoupon().getStatus(),
                        detail.userCoupon().getCreatedAt(),
                        detail.coupon().getExpiredAt(),
                        detail.userCoupon().getUsedAt()
                ))
                .collect(Collectors.toList());

        return new UserCouponResponse(couponItems);
    }
}

record UserCouponDetail(UserCoupon userCoupon, Coupon coupon){}
