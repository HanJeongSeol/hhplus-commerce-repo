package kr.hhplus.be.server.interfaces.point;
import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.infra.point.PointJpaRepository;
import kr.hhplus.be.server.infra.user.UserJpaRepository;
import kr.hhplus.be.server.interfaces.dto.point.request.PointChargeRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.concurrent.*;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class PointControllerConcurrencyIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private PointJpaRepository pointJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long testUserId;

    @BeforeEach
    void setUp(){
        // 데이터베이스 초기화
        pointJpaRepository.deleteAll();
        userJpaRepository.deleteAll();

        // 테스트 사용자 생성
        User user = User.builder()
                .userId(null)
                .name("설한정")
                .build();
        user = userJpaRepository.save(user);
        testUserId = user.getUserId();

        // 사용자 초기 포인트
        Point point = Point.builder()
                .userId(testUserId)
                .balance(0L)
                .build();
        pointJpaRepository.save(point);
    }

    @Test
    @DisplayName("동시성 테스트 - 사용자의 충전과 조회 요청")
    void 충전과_조회가_동시에_요청되면_충전된_금액이_조회된다() throws Exception{
        int iterations = 100;
        ExecutorService executor = Executors.newFixedThreadPool(2);
        CountDownLatch latch = new CountDownLatch(1);

        for(int i = 0; i<iterations; i++) {


            // 여러개의 충전 대기
            Callable<Void> chargeTask = () -> {
                latch.await(); // 동시 시작을 위한 대기 설정
                var chargeRequest = new PointChargeRequest(testUserId, 1000L);
                String requestJson = objectMapper.writeValueAsString(chargeRequest);
                mockMvc.perform(post("/api/v1/points/charge")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestJson))
                        .andExpect(status().isOk());
                return null;
            };

            // 조회 작업 정의
            Callable<String> getTask = () -> {
                latch.await();
                return mockMvc.perform(get("/api/v1/points/{userId}", testUserId))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
            };

            // 두 작업을 ExecutorService에 제출
            Future<Void> chargeFuture = executor.submit(chargeTask);
            Future<String> getFuture = executor.submit(getTask);

            latch.countDown();  // latch.await()으로 대기시킨 작업 돋시 실행
            chargeFuture.get(); // 충전 작업 완료 결과
            String getResponse = getFuture.get(); // 조회 작업 결과 획득 (동시성 요청 중의 조회)
        }

        // 최종 잔액 조회 및 검증
        mockMvc.perform(get("/api/v1/points/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.balance").value(100*1000L));

        executor.shutdown();  // ExecutorService 종료

    }

}
