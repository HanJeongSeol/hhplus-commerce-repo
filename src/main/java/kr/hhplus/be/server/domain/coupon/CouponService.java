package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.support.constant.CouponStatus;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CouponService {
    private final CouponRepository couponRepository;

    @Transactional
    public UserCoupon issueCoupon(User user, Long couponId) {
        Coupon coupon = couponRepository.findByIdWithLock(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));
        coupon.issue();
        couponRepository.save(coupon);
        UserCoupon userCoupon = UserCoupon.builder()
                .userId(user.getUserId())
                .couponId(couponId)
                .status(CouponStatus.AVAILABLE)
                .build();

        return couponRepository.save(userCoupon);
    }
    @Transactional
    public UserCoupon useCoupon(Long userId, Long couponId) {
        UserCoupon userCoupon = couponRepository.findUserCoupon(userId, couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));
        userCoupon.use();
        return couponRepository.save(userCoupon);
    }

    @Transactional
    public Coupon getCoupon(Long couponId){
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));
        return coupon;
    }

    @Transactional
    public List<UserCoupon> getUserCoupons(Long userId) {
        return couponRepository.findUserCoupons(userId);
    }
}
