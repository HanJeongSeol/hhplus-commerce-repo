package kr.hhplus.be.server.application.coupon.request;

import kr.hhplus.be.server.interfaces.dto.coupon.request.CouponRequest;

public class CouponCommand {
    public record IssueCoupon(
            Long userId,
            Long couponId
    ){
        public static IssueCoupon from(CouponRequest.IssueRequest request){
            return new IssueCoupon(
                    request.userId(),
                    request.couponId()
            );
        }
    }

    public record UserCouponInfo(
            Long userId
    ) {
        public static UserCouponInfo from(CouponRequest.UserCouponInfoRequest request){
            return new UserCouponInfo(
                    request.userId()
            );
        }
    }
}
