package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.dto.CouponInfo;
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

    /**
     * 쿠폰 발급
     */
    @Transactional
    public CouponInfo.IssueUserCoupon issueCoupon(Long userId, Long couponId) {
        // 쿠폰 테이블 쿠폰 조회
        Coupon coupon = couponRepository.findByIdWithLock(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

        System.out.println(couponId);

        // 사용자 중복 발급 확인
        if(couponRepository.findUserCoupon(userId, couponId).isPresent()){
            throw new BusinessException(ErrorCode.COUPON_ALREADY_ISSUED);
        }

        // 쿠폰 발급 처리
        coupon.issue();
        couponRepository.save(coupon);

        UserCoupon userCoupon = UserCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .status(CouponStatus.ACTIVE)
                .build();

        couponRepository.save(userCoupon);

        return CouponInfo.IssueUserCoupon.from(userCoupon, coupon);
    }

    /**
     * 쿠폰 사용
     */
    @Transactional
    public UserCoupon useCoupon(Long userId, Long couponId) {
        // 사용자 쿠폰 및 쿠폰 존재 확인
        UserCoupon userCoupon = couponRepository.findUserCoupon(userId, couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

        Coupon coupon = getCoupon(couponId);

        /**
         * 쿠폰 사용 처리
         * - 사용 전 Coupon.isExpired() 메서드를 사용하여 기간 만료시 예외 발생
         */
        userCoupon.use(coupon);
        couponRepository.save(userCoupon);

        return userCoupon;
    }

    /**
     * 쿠폰 상세 조회
     */
    @Transactional
    public Coupon getCoupon(Long couponId){
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

    }

    /**
     * 사용자 쿠폰 목록 조회
     */
    @Transactional
    public List<CouponInfo.UserCouponInfo> getUserCoupons(Long userId) {
        return couponRepository.findUserCoupons(userId).stream()
                .map(userCoupon -> {
                    Coupon coupon = couponRepository.findById(userCoupon.getCouponId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));
                    return CouponInfo.UserCouponInfo.from(userCoupon, coupon);
                })
                .toList();
    }
}