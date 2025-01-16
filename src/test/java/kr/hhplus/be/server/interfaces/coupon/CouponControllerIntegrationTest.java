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
public class CouponControllerIntegrationTest {
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private CouponJpaRepository couponJpaRepository;

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Autowired
    private ObjectMapper objectMapper;

    private Long testUserId;
    private Long testCouponId;

    @BeforeEach
    void setUp() {
        // 데이터베이스 초기화
        couponJpaRepository.deleteAll();
        userJpaRepository.deleteAll();

        User user = User.builder()
                .name("테스트사용자")
                .build();
        user = userJpaRepository.save(user);
        testUserId = user.getUserId();

        // 테스트 쿠폰 생성
        Coupon coupon = Coupon.builder()
                .name("테스트쿠폰")
                .discountPrice(1000L)
                .stock(10)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();
        coupon = couponJpaRepository.save(coupon);
        testCouponId = coupon.getCouponId();
    }

    @Test
    @DisplayName("[POST] /api/v1/coupons/issue - 쿠폰 발급 성공")
    void 사용자_아이디와_쿠폰_아이디를_전달하면_쿠폰이_발급된다() throws Exception{
        // given
        var issueRequest = new CouponRequest.IssueRequest(
                testUserId,
                testCouponId
        );
        String requestJson = objectMapper.writeValueAsString(issueRequest);

        // when & then
        mockMvc.perform(post("/api/v1/coupons/issue")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.userCouponId").exists())
                .andExpect(jsonPath("$.data.couponName").value("테스트쿠폰"))
                .andExpect(jsonPath("$.data.discountAmount").value(1000))
                .andExpect(jsonPath("$.data.status").exists());
    }

    @Test
    @DisplayName("[GET] /api/v1/coupons/users/{userId} - 사용자 쿠폰 목록 조회 성공")
    void 사용자_아이디와_쿠폰_아이디를_전달하면_사용자_보유쿠폰목록_조회_성공() throws Exception{
        // given -> 검증 시 여러개의 쿠폰 발급 목록을 확인하기 위한 작업
        List<Coupon> coupons = new ArrayList<>();
        int couponCount = 5;
        for(int i = 1; i<couponCount; i++){
            Coupon coupon = Coupon.builder()
                    .name("테스트쿠폰"+i)
                    .discountPrice(1000L*i)
                    .stock(10)
                    .expiredAt(LocalDateTime.now().plusDays(7))
                    .build();
            coupons.add(couponJpaRepository.save(coupon));
            var issueRequest = new CouponRequest.IssueRequest(
                    testUserId,
                    coupon.getCouponId()
            );
            String requestJson = objectMapper.writeValueAsString(issueRequest);
            mockMvc.perform(post("/api/v1/coupons/issue")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(requestJson))
                    .andExpect(status().isOk());
        }

        // when
        var response = mockMvc.perform(get("/api/v1/coupons/users/{userId}", testUserId));

        // then
        response.andExpect(status().isOk())
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(coupons.size()));

                for(int i = 0; i<coupons.size(); i++){
                    response.andExpect(jsonPath("$.data[" + i + "].couponName").value("테스트쿠폰" + (i + 1)))
                            .andExpect(jsonPath("$.data[" + i + "].discountPrice").value(1000L * (i + 1)));
                }

    }
}
