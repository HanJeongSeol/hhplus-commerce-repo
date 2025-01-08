package kr.hhplus.be.server.config;

import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.support.constant.CouponStatus;

import java.time.LocalDateTime;

public abstract class TestUtil {

    /**
     * 사용자 생성 및 초기 포인트 지급 후 연결
     */
    public static User createTestUser(){
        User user = User.builder()
                .userId(1L)
                .name("설한정")
                .build();

        user.initialPoint();

        return user;
    }

    public static Point createTestPoint(){
        return Point.builder()
                .pointId(1L)
                .balance(0L)
                .build();
    }

    public static Coupon createCoupon() {
        return Coupon.builder()
                .couponId(1L)
                .name("테스트 쿠폰")
                .discountAmount(1000L)
                .stock(10)
                .expiredAt(LocalDateTime.of(2025, 1, 11, 23, 59, 59))
                .status(CouponStatus.AVAILABLE)
                .build();
    }

    public static Coupon createNoStockCoupon() {
        return Coupon.builder()
                .couponId(2L)
                .name("테스트 쿠폰")
                .discountAmount(1000L)
                .stock(0)
                .expiredAt(LocalDateTime.of(2025, 1, 11, 23, 59, 59))
                .status(CouponStatus.AVAILABLE)
                .build();
    }


    public static UserCoupon createUserCoupon() {
        User testUser = createTestUser();
        Coupon testCoupon = createCoupon();

        return UserCoupon.builder()
                .userCouponId(1L)
                .userId(testUser.getUserId())
                .couponId(testCoupon.getCouponId())
                .status(CouponStatus.AVAILABLE)
                .coupon(testCoupon)
                .user(testUser)
                .build();
    }

}
