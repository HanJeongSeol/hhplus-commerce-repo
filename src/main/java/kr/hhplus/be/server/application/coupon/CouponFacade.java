package kr.hhplus.be.server.application.coupon;


import kr.hhplus.be.server.application.coupon.request.CouponCommand;
import kr.hhplus.be.server.application.coupon.response.CouponResult;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.dto.CouponInfo;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    /**
     * 사용자 쿠폰 목록 조회
     */
    public List<CouponResult.UserCouponResult> getUserCouponList(CouponCommand.UserCouponInfo command){
        // 사용자 존재 검증
        userService.getUserById(command.userId());

        // 사용자 쿠폰 목록 조회
        List<CouponInfo.UserCouponInfo> userCouponList = couponService.getUserCoupons(command.userId());

        return userCouponList.stream()
                .map(CouponResult.UserCouponResult::from)
                .toList();
    }

    /**
     * Redis를 사용한 비동기 쿠폰 발급 요청
     */
    public CouponResult.IssueRequestResult requestCouponIssue(CouponCommand.IssueCoupon command) {
        userService.getUserById(command.userId());

        try {
            couponService.requestCouponIssue(command.userId(), command.couponId());
            return CouponResult.IssueRequestResult.of("쿠폰 발급이 요청되었습니다.");
        } catch (BusinessException e) {
            return CouponResult.IssueRequestResult.of(e.getMessage());
        }
    }

}
