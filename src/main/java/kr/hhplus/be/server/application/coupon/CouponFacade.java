package kr.hhplus.be.server.application.coupon;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CouponFacade {
    private final UserService userService;
    private final CouponService couponService;

    @Transactional
    public CouponIssueResult issueCoupon(CouponIssueCommand request) {

        User user = userService.getUserById(request.userId());

        UserCoupon userCoupon = couponService.issueCoupon(user, request.couponId());

        Coupon coupon = couponService.getCoupon(request.couponId());

        return new CouponIssueResult(userCoupon, coupon);
    }

    public UserCouponListResult getUserCoupons(Long userId) {

        userService.getUserById(userId);

        List<UserCoupon> userCoupons = couponService.getUserCoupons(userId);

        List<UserCouponDetail> details = userCoupons.stream()
                .map(userCoupon -> {
                    Coupon coupon = couponService.getCoupon(userCoupon.getCouponId());
                    return new UserCouponDetail(userCoupon, coupon);
                })
                .collect(Collectors.toList());

        return new UserCouponListResult(details);
    }
}
