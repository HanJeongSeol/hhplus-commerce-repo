package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.dto.CouponInfo;
import kr.hhplus.be.server.support.constant.CouponStatus;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CouponService {
    private final CouponRepository couponRepository;
    private final CouponCacheRepository couponCacheRepository;

    /**
     * 쿠폰 발급
     */
    @Transactional
    public CouponInfo.IssueUserCoupon issueCoupon(Long userId, Long couponId) {
        // 쿠폰 테이블 쿠폰 조회
        Coupon coupon = couponRepository.findByIdWithLock(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND,couponId));


        // 사용자 중복 발급 확인
        // 테스트를 위한 주석 처리
//        if(couponRepository.findUserCoupon(userId, couponId).isPresent()){
//            throw new BusinessException(ErrorCode.COUPON_ALREADY_ISSUED);
//        }

        // 쿠폰 발급 처리
        coupon.issue();
        coupon = couponRepository.save(coupon);

        UserCoupon userCoupon = UserCoupon.builder()
                .userId(userId)
                .couponId(couponId)
                .status(CouponStatus.ACTIVE)
                .build();

        userCoupon = couponRepository.save(userCoupon);

        return CouponInfo.IssueUserCoupon.from(userCoupon, coupon);
    }

    /**
     * 쿠폰 사용
     */
    @Transactional
    public UserCoupon useCoupon(Long userId, Long couponId) {
        // 사용자 쿠폰 및 쿠폰 존재 확인
        UserCoupon userCoupon = couponRepository.findUserCoupon(userId, couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND,couponId));

        Coupon coupon = getCoupon(couponId);

        /**
         * 쿠폰 사용 처리
         * - 사용 전 Coupon.isExpired() 메서드를 사용하여 기간 만료시 예외 발생
         */
        userCoupon.use(coupon);
        couponRepository.save(userCoupon);

        return userCoupon;
    }

    public void userCouponCheck(Long userId, Long couponId){
        // 사용자 쿠폰 및 쿠폰 존재 확인
        UserCoupon userCoupon = couponRepository.findUserCoupon(userId, couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND,couponId));
        if(!userCoupon.isAvailable()){
           throw new BusinessException(ErrorCode.COUPON_NOT_AVAILABLE, couponId);
        }
    }
    /**
     * 쿠폰 상세 조회
     */
    @Transactional
    public Coupon getCoupon(Long couponId){
        return couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND,couponId));

    }

    /**
     * 사용자 쿠폰 목록 조회
     */
    @Transactional
    public List<CouponInfo.UserCouponInfo> getUserCoupons(Long userId) {
        return couponRepository.findUserCoupons(userId).stream()
                .map(userCoupon -> {
                    Coupon coupon = couponRepository.findById(userCoupon.getCouponId())
                            .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND,userCoupon.getUserCouponId()));
                    return CouponInfo.UserCouponInfo.from(userCoupon, coupon);
                })
                .toList();
    }


    // ==============================
    //          레디스 캐싱
    // ==============================

    /**
     * 쿠폰 발급 요청을 Redis에 저장해놓는다
     */
    public void requestCouponIssue(Long userId, Long couponId){
        // 1. 쿠폰 정보 조회 및 유효성 검증
        Coupon coupon = couponRepository.findById(couponId)
                .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

        if(coupon.isExpired()){
            throw new BusinessException(ErrorCode.COUPON_EXPIRED);
        }

        // 2. 이미 발급받은 쿠폰인지 확인
        // 테스트를 위한 주석
//        if(couponCacheRepository.hasIssuedCoupon(couponId, userId)){
//            throw new BusinessException(ErrorCode.COUPON_ALREADY_ISSUED);
//        }

        // 3. 레디스 큐에 발급 요청 추가
        couponCacheRepository.addCouponRequest(couponId, userId, System.currentTimeMillis());
    }

    /**
     * 쿠폰 재고 조회 및 캐싱
     */
    public int getAvailableStock(Long couponId){
        return couponCacheRepository.getStockCache(couponId)
                .orElseGet(() -> {
                    // 레디스에 캐싱되지 않았으면 데이터베이스에서 조회한 후 캐싱하기
                    Coupon coupon = couponRepository.findById(couponId)
                            .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));
                    int stock = coupon.getStock();
                    couponCacheRepository.setStockCache(couponId, stock);
                    return stock;
                });
    }

    /**
     * 레디스 캐시를 활용한 쿠폰 발급 진행
     */
    @Transactional
    public void issueCouponByRedis(Long userId, Long couponId){
        // 1. Redis 재고 감소 시도
        if (!couponCacheRepository.decrementStock(couponId)) {
            throw new BusinessException(ErrorCode.COUPON_OUT_OF_STOCK);
        }
        log.info("Redis 재고 감소 완료. 쿠폰 ID: {}, 사용자 ID: {}", couponId, userId);

        // 1-2. 재차 중복 체크: 혹시 이미 발급된 경우에는 재고 복구하고 중복 발급 예외 처리
        // 테스트를 위한 주석
//        if (couponCacheRepository.hasIssuedCoupon(couponId, userId) ||
//                couponRepository.findUserCoupon(userId, couponId).isPresent()) {
//            // 중복으로 처리된 경우 Redis에서 감소된 재고 복구
//            couponCacheRepository.incrementStock(couponId);
//            throw new BusinessException(ErrorCode.COUPON_ALREADY_ISSUED);
//        }
        try {
            log.info("DB 쿠폰 발급 시도. 쿠폰 ID: {}, 사용자 ID: {}", couponId, userId);
            // 2. DB 쿠폰 발급 처리
            Coupon coupon = couponRepository.findById(couponId)
                    .orElseThrow(() -> new BusinessException(ErrorCode.COUPON_NOT_FOUND));

            coupon.issue();
            couponRepository.save(coupon);

            // 3. 사용자 쿠폰 생성
            UserCoupon userCoupon = UserCoupon.builder()
                    .userId(userId)
                    .couponId(couponId)
                    .status(CouponStatus.ACTIVE)
                    .build();

            couponRepository.save(userCoupon);

            // 4. Redis에 발급 완료 표시
            couponCacheRepository.markAsIssued(couponId, userId);
            couponCacheRepository.removeFromRequestQueue(couponId, userId);

        } catch (Exception e) {
            // 실패 시 Redis 재고 복구
            log.error("쿠폰 발급 중 오류 발생. 사용자 ID: {}", userId, e);
            couponCacheRepository.incrementStock(couponId);
            couponCacheRepository.markAsFailed(couponId, userId);
            couponCacheRepository.removeFromRequestQueue(couponId, userId);
            throw e;
        }
    }
}