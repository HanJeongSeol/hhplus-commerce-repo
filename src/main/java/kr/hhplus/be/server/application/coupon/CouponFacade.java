package kr.hhplus.be.server.application.coupon;


import kr.hhplus.be.server.application.coupon.request.CouponCommand;
import kr.hhplus.be.server.application.coupon.response.CouponResult;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.dto.CouponInfo;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
@Service
@RequiredArgsConstructor
public class CouponFacade {

    private final UserService userService;
    private final CouponService couponService;

    /**
     * 쿠폰 발급
     */

    public CouponResult.IssueResult issueCoupon(CouponCommand.IssueCoupon command){
        userService.getUserById(command.userId());
        CouponInfo.IssueUserCoupon issueUserCoupon = couponService.issueCoupon(command.userId(), command.couponId());
        return CouponResult.IssueResult.from(issueUserCoupon);
    }
}
