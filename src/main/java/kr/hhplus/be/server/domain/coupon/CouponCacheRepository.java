package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.dto.CouponIssueRequest;

import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface CouponCacheRepository {

    // 쿠폰 발급 요청 관련
    void addCouponRequest(Long couponId, Long userId, long timestamp);
    List<CouponIssueRequest> getPendingRequests(Long couponId, int count);
    void removeFromRequestQueue(Long couponId, Long userId);

    // 발급 상태 관리
    boolean hasIssuedCoupon(Long couponId, Long userId);
    void markAsIssued(Long couponId, Long userId);
    void markAsFailed(Long couponId, Long userId);

    // 쿠폰 재고 관리
    Optional<Integer> getStockCache(Long couponId);
    void setStockCache(Long couponId, int stock);

    void incrementStock(Long couponId);
    boolean decrementStock(Long couponId);
    Set<String> findActiveCouponKeys();

}

