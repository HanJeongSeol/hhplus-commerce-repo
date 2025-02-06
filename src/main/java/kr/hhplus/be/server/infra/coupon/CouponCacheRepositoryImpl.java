package kr.hhplus.be.server.infra.coupon;

import kr.hhplus.be.server.domain.coupon.CouponCacheRepository;
import kr.hhplus.be.server.domain.coupon.dto.CouponIssueRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponCacheRepositoryImpl implements CouponCacheRepository {
    private final RedisTemplate<String, Object> redisTemplate;

    // 숫자 연산 및 재고 관련 작업에 사용할 템플릿
    @Qualifier("redisNumericTemplate")
    private final RedisTemplate<String, String> redisNumericTemplate;
    // 쿠폰 요청 큐
    private static final String KEY_COUPON_REQUESTS = "coupon:%d:requests";
    // 발급 완료된 쿠폰 목록
    private static final String KEY_COUPON_ISSUED = "coupon:%d:issued";
    // 발급 실패한 쿠폰 목록
    private static final String KEY_COUPON_FAILED = "coupon:%d:failed";
    // 쿠폰 재고 정보
    private static final String KEY_COUPON_STOCK = "coupon:%d:stock";
    // TTL 1시간 설정
    private static final long CACHE_TTL = 60 * 60;

    /**
     * 쿠폰 요청 시 Redis 정렬 ZSet 추가
     * key값ㅂ으로 couponId, userId
     * score로 timestamp 사용
     */
    @Override
    public void addCouponRequest(Long couponId, Long userId, long timestamp) {
        String requestKey = String.format(KEY_COUPON_REQUESTS, couponId);
        redisTemplate.opsForZSet().add(requestKey, String.valueOf(userId), timestamp);
    }

    /**
     * 대기중인 쿠폰 발급 요청 조회
     */
    @Override
    public List<CouponIssueRequest> getPendingRequests(Long couponId, int count) {
        String requestKey = String.format(KEY_COUPON_REQUESTS, couponId);
        // Redis ZSet에서 요청된 사용자 아이디와 타임스탬프 조회 , 타임스탬프를 score로 사용 중
        Set<ZSetOperations.TypedTuple<Object>> tuples =
                redisTemplate.opsForZSet().rangeWithScores(requestKey, 0, count - 1);
        // 조회한 데이터를 CouponIssueRequest 객체 리스트로 변환
        return tuples.stream()
                .map(tuple -> new CouponIssueRequest(
                        couponId,
                        Long.parseLong(String.valueOf(tuple.getValue())),
                        tuple.getScore().longValue()))
                .collect(Collectors.toList());
    }

    /**
     * 쿠폰 요청 큐에서 특정 사용자의 요청 제거
     */
    @Override
    public void removeFromRequestQueue(Long couponId, Long userId) {
        String requestKey = String.format(KEY_COUPON_REQUESTS, couponId);
        redisTemplate.opsForZSet().remove(requestKey, String.valueOf(userId));
    }

    /**
     * 사용자가 쿠폰을 발급받았는지 확인
     */
    @Override
    public boolean hasIssuedCoupon(Long couponId, Long userId) {
        String issuedKey = String.format(KEY_COUPON_ISSUED, couponId);
        Boolean isMember = redisTemplate.opsForSet().isMember(issuedKey, String.valueOf(userId));
        return Boolean.TRUE.equals(isMember);
    }

    /**
     * 사용자를 발급 완료된 쿠폰 목록에 추가
     */
    @Override
    public void markAsIssued(Long couponId, Long userId) {
        String issuedKey = String.format(KEY_COUPON_ISSUED, couponId);
        redisTemplate.opsForSet().add(issuedKey, userId);
    }

    /**
     * 사용자를 발급 실패 목록에 추가
     */
    @Override
    public void markAsFailed(Long couponId, Long userId) {
        String failedKey = String.format(KEY_COUPON_FAILED, couponId);
        redisTemplate.opsForSet().add(failedKey, userId);
    }

    /**
     * 쿠폰 재고를 조회
     */
    @Override
    public Optional<Integer> getStockCache(Long couponId) {
        String stockKey = String.format(KEY_COUPON_STOCK, couponId);
        String value = redisNumericTemplate.opsForValue().get(stockKey);
        if (value == null) return Optional.empty();
        try {
            return Optional.of(Integer.parseInt(value));
        } catch (NumberFormatException e) {
            log.error("재고 값 조회 실패. 키 : {}: {}", stockKey, value);
            return Optional.empty();
        }
    }

    /**
     * 쿠폰 재고를 캐시에 추가
     */
    @Override
    public void setStockCache(Long couponId, int stock) {
        String stockKey = String.format(KEY_COUPON_STOCK, couponId);
        redisNumericTemplate.opsForValue().set(stockKey, String.valueOf(stock), CACHE_TTL, TimeUnit.SECONDS);

    }

    /**
     * 쿠폰 재고 증가, 발급 실패 시 복원 용도
     */
    @Override
    public void incrementStock(Long couponId) {
        String stockKey = String.format(KEY_COUPON_STOCK, couponId);
        redisNumericTemplate.opsForValue().increment(stockKey);
    }

    /**
     * 쿠폰 재고 감소
     */
    @Override
    public boolean decrementStock(Long couponId) {
        String stockKey = String.format(KEY_COUPON_STOCK, couponId);
        // 먼저 키 존재 여부 확인
        if (!Boolean.TRUE.equals(redisNumericTemplate.hasKey(stockKey))) {
            // 키가 없으면 재고 캐시가 초기화되지 않은 상태로 판단하여 false 반환
            return false;
        }
        Long currentStock = redisNumericTemplate.opsForValue()
                .decrement(stockKey);
        return currentStock != null && currentStock >= 0;
    }

    /**
     * 쿠폰 발급 요청 키 목록 반환
     */
    @Override
    public Set<String> findActiveCouponKeys() {
        // 요청 큐에 해당하는 모든 키 반환
        Set<String> keys = redisTemplate.keys("coupon:*:requests");
        return keys != null ? keys : Collections.emptySet();
    }}
