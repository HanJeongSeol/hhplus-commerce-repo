package kr.hhplus.be.server;


import kr.hhplus.be.server.application.coupon.CouponFacade;
import kr.hhplus.be.server.application.coupon.request.CouponCommand;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.coupon.CouponRepository;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import kr.hhplus.be.server.infra.coupon.CouponCacheRepositoryImpl;
import kr.hhplus.be.server.support.schedulers.CouponIssueScheduler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class CouponIssueRedisTest {
    @Autowired
    private CouponFacade couponFacade;

    @Autowired
    private CouponIssueScheduler couponIssueScheduler;

    @Autowired
    private CouponRepository couponRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CouponCacheRepositoryImpl couponCacheRepository;

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    private List<Long> userIds;
    private Long testCouponId;

    @BeforeEach
    public void setup() {
        // 1. 테스트용 사용자 생성 (100명), 쿠폰 수 보다 많은 유저 수로 테스트 진행
        userIds = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            User user = User.builder()
                    .name("TestUser" + i)
                    .build();
            userRepository.save(user);
            userIds.add(user.getUserId());
        }

        // 2. 테스트용 쿠폰 생성, 재고 10개 고정
        Coupon coupon = Coupon.builder()
                .name("테스트 쿠폰")
                .discountPrice(1000L)
                .stock(10)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();
        coupon = couponRepository.save(coupon);
        testCouponId = coupon.getCouponId();

        // 3. Redis 초기화
        couponCacheRepository.setStockCache(testCouponId, coupon.getStock());
        redisTemplate.delete(String.format("coupon:%d:issued", testCouponId));
        redisTemplate.delete(String.format("coupon:%d:requests", testCouponId));
    }

    @Test
    public void 재고가_10개인_쿠폰에_사용자_50명_동시_발급_요청시_선착순_10명_발급_나머지_40명_실패() throws InterruptedException {
        int concurrentUsers = 50;
        CountDownLatch latch = new CountDownLatch(concurrentUsers);
        ExecutorService executor = Executors.newFixedThreadPool(concurrentUsers);

        // 동시 요청 실행
        for (int i = 0; i < concurrentUsers; i++) {
            final Long userId = userIds.get(i);
            executor.submit(() -> {
                try {
                    couponFacade.requestCouponIssue(
                            new CouponCommand.IssueCoupon(userId, testCouponId)
                    );
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await(5, TimeUnit.SECONDS);
        executor.shutdown();

        // 스케줄러 실행
        couponIssueScheduler.processCouponRequests();
        Thread.sleep(2000); // 처리 대기

        // 결과 검증
        int issuedCount = getIssuedCount(testCouponId);
        assertEquals(10, issuedCount, "발급된 쿠폰 수가 재고량과 일치해야 합니다.");

        Optional<Integer> remainingStock = couponCacheRepository.getStockCache(testCouponId);
        assertTrue(remainingStock.isPresent());
        assertEquals(0, remainingStock.get(), "남은 재고가 0이어야 합니다.");
    }

    private int getIssuedCount(Long couponId) {
        String issuedKey = String.format("coupon:%d:issued", couponId);
        Long size = redisTemplate.opsForSet().size(issuedKey);
        return size != null ? size.intValue() : 0;
    }
}
