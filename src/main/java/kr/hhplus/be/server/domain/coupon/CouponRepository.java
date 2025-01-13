package kr.hhplus.be.server.domain.coupon;

import java.util.List;
import java.util.Optional;

public interface CouponRepository {
    Optional<Coupon> findById(Long couponId);

    Optional<Coupon> findByIdWithLock(Long couponId);

    Optional<UserCoupon> findUserCoupon(Long userId, Long couponId);

    List<UserCoupon> findUserCoupons(Long userId);

    Coupon save(Coupon coupon);

    UserCoupon save(UserCoupon userCoupon);
}
