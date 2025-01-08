package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.config.TestUtil;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.support.constant.CouponStatus;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.api.Nested;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("쿠폰 서비스 테스트")
public class CouponServiceUnitTest {
    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    private User testUser;
    private Coupon testCoupon;
    private UserCoupon testUserCoupon;

    @BeforeEach
    void setUp() {
        testUser = TestUtil.createTestUser();
        testCoupon = TestUtil.createCoupon();
        testUserCoupon = TestUtil.createUserCoupon();
    }

    @Nested
    @DisplayName("쿠폰 발급")
    class IssueCoupon {

        @Test
        @DisplayName("쿠폰 발급 성공")
        void success() {
            // given
            given(couponRepository.findByIdWithLock(testCoupon.getCouponId()))
                    .willReturn(Optional.of(testCoupon));

            given(couponRepository.save(any(Coupon.class)))
                    .willAnswer(invocation -> invocation.getArgument(0));

            given(couponRepository.save(any(UserCoupon.class)))
                    .willReturn(testUserCoupon);

            // when
            UserCoupon result = couponService.issueCoupon(testUser, testCoupon.getCouponId());

            // then
            assertThat(result.getUserId()).isEqualTo(testUser.getUserId());
            assertThat(result.getCouponId()).isEqualTo(testCoupon.getCouponId());
            assertThat(result.getStatus()).isEqualTo(CouponStatus.AVAILABLE);
            verify(couponRepository).save(any(Coupon.class));
        }

        @Test
        @DisplayName("존재하지 않는 쿠폰")
        void failCouponNotFound() {
            // given
            given(couponRepository.findByIdWithLock(999L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> couponService.issueCoupon(testUser, 999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.COUPON_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("쿠폰 재고 없음")
        void failNoStock() {
            // given
            Coupon noStockCoupon = TestUtil.createNoStockCoupon();

            given(couponRepository.findByIdWithLock(noStockCoupon.getCouponId()))
                    .willReturn(Optional.of(noStockCoupon));

            // when & then
            assertThatThrownBy(() -> couponService.issueCoupon(testUser, noStockCoupon.getCouponId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.COUPON_OUT_OF_STOCK.getMessage());
        }

        @Test
        @DisplayName("만료된 쿠폰")
        void failExpired() {
            // given
            Coupon expiredCoupon = Coupon.builder()
                    .couponId(testCoupon.getCouponId())
                    .name(testCoupon.getName())
                    .discountAmount(testCoupon.getDiscountAmount())
                    .stock(testCoupon.getStock())
                    .expiredAt(LocalDateTime.now().minusDays(1))
                    .status(CouponStatus.AVAILABLE)
                    .build();

            given(couponRepository.findByIdWithLock(expiredCoupon.getCouponId()))
                    .willReturn(Optional.of(expiredCoupon));

            // when & then
            assertThatThrownBy(() -> couponService.issueCoupon(testUser, expiredCoupon.getCouponId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.COUPON_EXPIRED.getMessage());
        }
    }

    @Nested
    @DisplayName("쿠폰 사용")
    class UseCoupon {

        @Test
        @DisplayName("정상적인 쿠폰 사용")
        void success() {
            // given
            given(couponRepository.findUserCoupon(testUser.getUserId(), testCoupon.getCouponId()))
                    .willReturn(Optional.of(testUserCoupon));
            given(couponRepository.save(any(UserCoupon.class)))
                    .willReturn(testUserCoupon);

            // when
            UserCoupon result = couponService.useCoupon(testUser.getUserId(), testCoupon.getCouponId());

            // then
            assertThat(result.getStatus()).isEqualTo(CouponStatus.USED);
            assertThat(result.getUsedAt()).isNotNull();
        }

        @Test
        @DisplayName("존재하지 않는 사용자 쿠폰")
        void failUserCouponNotFound() {
            // given
            given(couponRepository.findUserCoupon(999L, testCoupon.getCouponId()))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> couponService.useCoupon(999L, testCoupon.getCouponId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.COUPON_NOT_FOUND.getMessage());
        }

        @Test
        @DisplayName("이미 사용된 쿠폰")
        void failAlreadyUsed() {
            // given
            UserCoupon usedCoupon = UserCoupon.builder()
                    .userCouponId(testUserCoupon.getUserCouponId())
                    .userId(testUserCoupon.getUserId())
                    .couponId(testUserCoupon.getCouponId())
                    .status(CouponStatus.USED)
                    .usedAt(LocalDateTime.now().minusDays(1))
                    .coupon(testUserCoupon.getCoupon())
                    .user(testUserCoupon.getUser())
                    .build();

            given(couponRepository.findUserCoupon(testUser.getUserId(), testCoupon.getCouponId()))
                    .willReturn(Optional.of(usedCoupon));

            // when & then
            assertThatThrownBy(() -> couponService.useCoupon(testUser.getUserId(), testCoupon.getCouponId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.COUPON_NOT_AVAILABLE.getMessage());
        }
    }

    @Nested
    @DisplayName("사용자 쿠폰 목록 조회")
    class GetUserCoupons {

        @Test
        @DisplayName("사용자 쿠폰 목록 조회")
        void success() {
            // given
            List<UserCoupon> userCoupons = List.of(testUserCoupon);
            given(couponRepository.findUserCoupons(testUser.getUserId()))
                    .willReturn(userCoupons);

            // when
            List<UserCoupon> result = couponService.getUserCoupons(testUser.getUserId());

            // then
            assertThat(result).hasSize(1);
            assertThat(result.get(0).getUserId()).isEqualTo(testUser.getUserId());
            assertThat(result.get(0).getStatus()).isEqualTo(CouponStatus.AVAILABLE);
        }
    }
}
