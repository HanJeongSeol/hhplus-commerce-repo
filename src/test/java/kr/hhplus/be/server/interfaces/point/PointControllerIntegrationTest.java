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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@SpringBootTest
@AutoConfigureMockMvc
public class PointControllerIntegrationTest {

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
    @DisplayName("[POST] /api/v1/points/charge - 포인트 충전 성공 테스트")
    void userId와_amount_50000을_전달하면_충전_성공() throws Exception{
        // given
        var chargeRequest = new PointChargeRequest(
                testUserId,
                50000L
        );

        String requestJson = objectMapper.writeValueAsString(chargeRequest);

        // when
        var response = mockMvc.perform(post("/api/v1/points/charge")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson));
        // then
        response.andExpect(status().isOk())
                // API 응답 검증 : 성공 코드 및 응답 데이터 확인
                .andExpect(jsonPath("$.data.userId").value(testUserId.intValue()))
                .andExpect(jsonPath("$.data.chargedAmount").value(50000));
    }

    @Test
    @DisplayName("[GET] /api/v1/points/{userId} - 사용자 포인트 조회 성공 테스트")
    void userId가_전달되면_해당_사용자의_포인트_조회에_성공() throws Exception{
        // given
        var chargeRequest = new PointChargeRequest(testUserId, 50000L);
        String requestJson = objectMapper.writeValueAsString(chargeRequest);

        mockMvc.perform(post("/api/v1/points/charge")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk());

        // When & Then: 포인트 조회 요청 및 검증
        mockMvc.perform(get("/api/v1/points/{userId}", testUserId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userId").value(testUserId.intValue()))
                .andExpect(jsonPath("$.data.balance").value(50000));
    }
}
