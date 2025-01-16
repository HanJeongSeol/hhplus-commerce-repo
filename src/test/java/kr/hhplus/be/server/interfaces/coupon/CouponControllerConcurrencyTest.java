package kr.hhplus.be.server.interfaces.coupon;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infra.coupon.CouponJpaRepository;
import kr.hhplus.be.server.infra.user.UserJpaRepository;
import kr.hhplus.be.server.interfaces.dto.coupon.request.CouponRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultActions;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class CouponControllerConcurrencyTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("동시성 테스트 - 쿠폰 발급, 10명 성공, 11번째 실패")
    void 재고가_10개인_쿠폰에_11명이_발급신청하면_11번쨰_사용자는_발급_실패() throws Exception{
        // 테스트 데이터 설정, 쿠폰 재고 10개 & 11명 사용자 생성
        Long couponId = createTestCoupon(10);
        List<Long> userIds = createTestUsers(11);

        int threadCount = 11;
        ExecutorService executor = Executors.newFixedThreadPool(threadCount);
        CountDownLatch latch = new CountDownLatch(1);

        // 스레드마다 쿠폰 발급 요청을 수행하도록 정의 <- 나중에 동시에 실행함
        List<Callable<ResultActions>> tasks = new ArrayList<>();
        for(Long userId : userIds){
            tasks.add(() -> {
                latch.await();
                var issueRequest = new CouponRequest.IssueRequest(
                        userId,
                        couponId
                );
                String requestJson = objectMapper.writeValueAsString(issueRequest);
                return mockMvc.perform(post("/api/v1/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson));
            });
        }

        // 모든 작업 제출
        List<Future<ResultActions>> futures = new ArrayList<>();
        for(Callable<ResultActions> task : tasks){
            futures.add(executor.submit(task));
        }

        latch.countDown();
        executor.shutdown();
        executor.awaitTermination(30, TimeUnit.SECONDS);

        // 검증을 위한 결과 수집 및 성공,실패 카운트
        int successCount = 0;
        int failCount = 0;

        for(Future<ResultActions> future : futures){
            try{
                MvcResult result = future.get().andReturn();
                int status = result.getResponse().getStatus();
                if(HttpStatus.OK.value() == status){
                    successCount++;
                } else {
                    failCount++;
                }
            } catch (ExecutionException e){
                failCount++;
            }
        }
        assertThat(successCount).isEqualTo(10);
        assertThat(failCount).isEqualTo(1);
    }

    // 테스트 쿠폰 생성 메서드
    private Long createTestCoupon(int stock){
        Coupon coupon = Coupon.builder()
                .name("테스트쿠폰")
                .discountPrice(1000L)
                .stock(stock)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        Coupon savedCoupon = couponJpaRepository.save(coupon);
        return savedCoupon.getCouponId();
    }

    // 테스트 사용자 생성 메서드
    private List<Long> createTestUsers(int count){
        List<Long> userIds = new ArrayList<>();
        for(int i = 0 ; i<count ; i++){
            User user = User.builder()
                    .name("사용자" + i)
                    .build();
            user = userJpaRepository.save(user);
            userIds.add(user.getUserId());
        }
        return userIds;
    }
}
