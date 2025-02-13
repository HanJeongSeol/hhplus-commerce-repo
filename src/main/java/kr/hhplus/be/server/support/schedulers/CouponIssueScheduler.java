package kr.hhplus.be.server.support.schedulers;

import kr.hhplus.be.server.domain.coupon.CouponCacheRepository;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.coupon.dto.CouponIssueRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
@Slf4j
public class CouponIssueScheduler {
    private final CouponService couponService;
    private final CouponCacheRepository couponCacheRepository;

    /**
     * 1초마다 스케줄러를 돌려서 대기중인 쿠폰 발급 요청을 처리
     * 돌려돌려 돌림판..
     */
    @Scheduled(fixedDelay = 1000)
    public void processCouponRequests() {
        try {
            // 활성화된 쿠폰 키 목록 조회 <- 레디스 캐시
            Set<String> couponKeys = couponCacheRepository.findActiveCouponKeys();
            // 쿠폰 키에서 아이디 추출
            List<Long> couponIds = extractCouponIds(couponKeys);
            // 각 쿠폰 아이디에 대해서 대기중인 발급 요청 처리
            for (Long couponId : couponIds) {
                processRequestsForCoupon(couponId);
            }
        } catch (Exception e) {
            log.error("쿠폰 발급 스케줄러 실행 중 오류 발생: {}", e.getMessage(), e);
        }
    }

    /**
     * 쿠폰 아이디에 대한 발급 대기 요청을 처리하는 메서드
     */
    private void processRequestsForCoupon(Long couponId) {
        try {
            // 재고 캐시가 없으면 DB에서 재고를 읽어와 Redis에 저장 (초기화 진행)
            int availableStock = couponService.getAvailableStock(couponId);
            if (availableStock <= 0) {
                log.info("쿠폰 {} 재고 소진", couponId);
                // 재고가 소진된 경우, pending request들을 모두 실패 처리 및 큐에서 제거
                List<CouponIssueRequest> pendingRequests = couponCacheRepository.getPendingRequests(couponId, 1000);
                for (CouponIssueRequest request : pendingRequests) {
                    couponCacheRepository.markAsFailed(couponId, request.getUserId());
                    couponCacheRepository.removeFromRequestQueue(couponId, request.getUserId());
                }
                return;
            }

            // 대기 중인 요청 처리 -> 한 번에 10개의 대기 요청을 처리하도록 수행
            List<CouponIssueRequest> requests = couponCacheRepository.getPendingRequests(couponId, 10);

            for (CouponIssueRequest request : requests) {
                try {
                    // issueCouponByRedis 내부에서 재고 감소를 수행하므로,
                    // 스케줄러에서는 단순히 해당 요청에 대해 처리 호출
                    couponService.issueCouponByRedis(request.getUserId(), request.getCouponId());
                } catch (Exception e) {
                    log.error("사용자 {}의 쿠폰 발급 요청 처리 실패: {}",
                            request.getUserId(), e.getMessage());
                    // 실패 시 요청 큐에서 제거 및 실패 처리
                    couponCacheRepository.markAsFailed(request.getCouponId(), request.getUserId());
                    couponCacheRepository.removeFromRequestQueue(request.getCouponId(), request.getUserId());
                }
            }
        } catch (Exception e) {
            log.error("쿠폰 {} 처리 중 오류 발생: {}", couponId, e.getMessage(), e);
        }
    }

    /**
     * 레디스에서 조회한 쿠폰 키 목록에서 쿠폰 아이디를 추출
     * key = coupon:1:requests ,
     */
    private List<Long> extractCouponIds(Set<String> keys) {
        return keys.stream()
                // 쿠폰 ID 추출
                .map(key -> key.split(":")[1])
                // Long 타입으로 변환
                .map(Long::parseLong)
                .collect(Collectors.toList());
    }
}
