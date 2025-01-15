package kr.hhplus.be.server.domain.coupon.dto;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.support.constant.CouponStatus;

import java.time.LocalDateTime;

public class CouponInfo {
    public record IssueUserCoupon(
            Long userCouponId,
            String couponName,
            Long discountPrice,
            CouponStatus status,
            LocalDateTime issuedAt,
            LocalDateTime expiredAt
    ){
        public static IssueUserCoupon from(UserCoupon userCoupon, Coupon coupon){
            return new IssueUserCoupon(
                    userCoupon.getUserCouponId(),
                    coupon.getName(),
                    coupon.getDiscountPrice(),
                    userCoupon.getStatus(),
                    userCoupon.getCreatedAt(),
                    coupon.getExpiredAt()
            );
        }
    }

    public record UserCouponInfo(
            Long userCouponId,
            String couponName,
            Long discountPrice,
            CouponStatus status,
            LocalDateTime issuedAt,
            LocalDateTime expiredAt,
            LocalDateTime usedAt
    ) {
        public static UserCouponInfo from(UserCoupon userCoupon, Coupon coupon){
            return new UserCouponInfo(
                    userCoupon.getUserCouponId(),
                    coupon.getName(),
                    coupon.getDiscountPrice(),
                    userCoupon.getStatus(),
                    userCoupon.getCreatedAt(),
                    coupon.getExpiredAt(),
                    userCoupon.getUsedAt()
            );
        }
    }
}
