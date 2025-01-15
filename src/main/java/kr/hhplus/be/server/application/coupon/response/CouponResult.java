package kr.hhplus.be.server.application.coupon.response;

import kr.hhplus.be.server.domain.coupon.dto.CouponInfo;
import kr.hhplus.be.server.support.constant.CouponStatus;

import java.time.LocalDateTime;

public class CouponResult {
    public record IssueResult(
            Long userCouponId,
            String couponName,
            Long discountPrice,
            CouponStatus status,
            LocalDateTime issuedAt,
            LocalDateTime expiredAt
    ) {
        public static IssueResult from(CouponInfo.IssueUserCoupon issueUserCoupon){
            return new IssueResult(
                    issueUserCoupon.userCouponId(),
                    issueUserCoupon.couponName(),
                    issueUserCoupon.discountPrice(),
                    issueUserCoupon.status(),
                    issueUserCoupon.issuedAt(),
                    issueUserCoupon.expiredAt()
            );
        }
    }

    public record UserCouponResult(
            Long userCouponId,
            String couponName,
            Long discountPrice,
            CouponStatus status,
            LocalDateTime issuedAt,
            LocalDateTime expiredAt,
            LocalDateTime usedAt
    ) {
        public static UserCouponResult from(CouponInfo.UserCouponInfo userCoupon){
            return new UserCouponResult(
                    userCoupon.userCouponId(),
                    userCoupon.couponName(),
                    userCoupon.discountPrice(),
                    userCoupon.status(),
                    userCoupon.issuedAt(),
                    userCoupon.expiredAt(),
                    userCoupon.usedAt()
            );
        }
    }
}
