package kr.hhplus.be.server.application.coupon.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
}
