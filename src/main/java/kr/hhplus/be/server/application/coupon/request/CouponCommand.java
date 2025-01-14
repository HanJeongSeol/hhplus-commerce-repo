package kr.hhplus.be.server.application.coupon.request;

import kr.hhplus.be.server.interfaces.dto.coupon.request.CouponRequest;

public class CouponCommand {
    public record IssueCoupon(
            Long userId,
            Long couponId
    ){
        public static IssueCoupon from(CouponRequest.IssueRequest command){
            return new IssueCoupon(
                    command.userId(),
                    command.userId()
            );
        }
    }
}
