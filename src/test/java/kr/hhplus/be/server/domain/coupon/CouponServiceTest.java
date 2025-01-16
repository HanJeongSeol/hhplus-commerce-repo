package kr.hhplus.be.server.domain.coupon;

import kr.hhplus.be.server.domain.coupon.dto.CouponInfo;
import kr.hhplus.be.server.support.constant.CouponStatus;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("쿠폰 서비스 테스트")
class CouponServiceTest {

    @InjectMocks
    private CouponService couponService;

    @Mock
    private CouponRepository couponRepository;

    private Long testUserId;
    private Long testCouponId;
    private Coupon testCoupon;
    private UserCoupon testUserCoupon;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
        testCouponId = 1L;

        testCoupon = Coupon.builder()
                .couponId(testCouponId)
                .name("신규 가입 할인 쿠폰")
                .discountPrice(5000L)
                .stock(100)
                .expiredAt(LocalDateTime.now().plusDays(7))
                .build();

        testUserCoupon = UserCoupon.builder()
                .userCouponId(1L)
                .userId(testUserId)
                .couponId(testCouponId)
                .status(CouponStatus.ACTIVE)
                .build();
    }

    @Nested
    @DisplayName("쿠폰 발급")
    class IssueCoupon {
        @Test
        @DisplayName("쿠폰 발급 성공")
        void issueCouponSuccess() {
            // given
            Long userId = 1L;
            Long couponId = 1L;

            // Mock 데이터 생성
            Coupon mockCoupon = Coupon.builder()
                    .couponId(couponId)
                    .name("설날 쿠폰")
                    .discountPrice(5000L)
                    .stock(100)
                    .expiredAt(LocalDateTime.now().plusDays(10))
                    .build();

            UserCoupon mockUserCoupon = UserCoupon.builder()
                    .userCouponId(1L)
                    .userId(userId)
                    .couponId(couponId)
                    .status(CouponStatus.ACTIVE)
                    .build();

            // Mock 설정
            when(couponRepository.findByIdWithLock(couponId)).thenReturn(Optional.of(mockCoupon));
            when(couponRepository.findUserCoupon(userId, couponId)).thenReturn(Optional.empty());
            when(couponRepository.save(any(Coupon.class))).thenReturn(mockCoupon);
            when(couponRepository.save(any(UserCoupon.class))).thenReturn(mockUserCoupon);

            // when
            CouponInfo.IssueUserCoupon result = couponService.issueCoupon(userId, couponId);

            // then
            assertNotNull(result);
            assertEquals(userId, result.userCouponId());
            assertEquals(mockCoupon.getName(), result.couponName());
            assertEquals(mockCoupon.getDiscountPrice(), result.discountPrice());
            assertEquals(CouponStatus.ACTIVE, result.status());

            // Verify
            verify(couponRepository, times(1)).findByIdWithLock(couponId);
            verify(couponRepository, times(1)).findUserCoupon(userId, couponId);
            verify(couponRepository, times(1)).save(any(UserCoupon.class));
        }

        @Test
        @DisplayName("이미 발급된 쿠폰 발급 시도시 예외 발생")
        void throwExceptionWhenAlreadyIssued() {
            // given
            given(couponRepository.findByIdWithLock(testCouponId))
                    .willReturn(Optional.of(testCoupon));
            given(couponRepository.findUserCoupon(testUserId, testCouponId))
                    .willReturn(Optional.of(testUserCoupon));

            // when & then
            assertThatThrownBy(() -> couponService.issueCoupon(testUserId, testCouponId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COUPON_ALREADY_ISSUED);
        }

        @Test
        @DisplayName("만료된 쿠폰 발급 시도시 예외 발생")
        void throwExceptionWhenExpired() {
            // given
            Coupon expiredCoupon = Coupon.builder()
                    .couponId(testCouponId)
                    .name("만료된 쿠폰")
                    .discountPrice(5000L)
                    .stock(100)
                    .expiredAt(LocalDateTime.now().minusDays(1))
                    .build();

            given(couponRepository.findByIdWithLock(testCouponId))
                    .willReturn(Optional.of(expiredCoupon));
            given(couponRepository.findUserCoupon(testUserId, testCouponId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> couponService.issueCoupon(testUserId, testCouponId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COUPON_EXPIRED);
        }

        @Test
        @DisplayName("재고가 없는 쿠폰 발급 시도시 예외 발생")
        void throwExceptionWhenOutOfStock() {
            // given
            Coupon noStockCoupon = Coupon.builder()
                    .couponId(testCouponId)
                    .name("재고 없는 쿠폰")
                    .discountPrice(5000L)
                    .stock(0)
                    .expiredAt(LocalDateTime.now().plusDays(7))
                    .build();

            given(couponRepository.findByIdWithLock(testCouponId))
                    .willReturn(Optional.of(noStockCoupon));
            given(couponRepository.findUserCoupon(testUserId, testCouponId))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> couponService.issueCoupon(testUserId, testCouponId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COUPON_OUT_OF_STOCK);
        }
    }

    @Nested
    @DisplayName("쿠폰 사용")
    class UseCoupon {
        @Test
        @DisplayName("쿠폰 사용 성공")
        void useCouponSuccess() {
            // given
            given(couponRepository.findUserCoupon(testUserId, testCouponId))
                    .willReturn(Optional.of(testUserCoupon));
            given(couponRepository.findById(testCouponId))
                    .willReturn(Optional.of(testCoupon));
            given(couponRepository.save(any(UserCoupon.class)))
                    .willReturn(testUserCoupon);

            // when
            UserCoupon result = couponService.useCoupon(testUserId, testCouponId);

            // then
            assertThat(result.getStatus()).isEqualTo(CouponStatus.USED);
            assertThat(result.getUsedAt()).isNotNull();
            verify(couponRepository).save(any(UserCoupon.class));
        }

        @Test
        @DisplayName("이미 사용된 쿠폰 사용 시도시 예외 발생")
        void throwExceptionWhenAlreadyUsed() {
            // given
            UserCoupon usedCoupon = UserCoupon.builder()
                    .userCouponId(1L)
                    .userId(testUserId)
                    .couponId(testCouponId)
                    .status(CouponStatus.USED)
                    .usedAt(LocalDateTime.now().minusHours(1))
                    .build();

            given(couponRepository.findUserCoupon(testUserId, testCouponId))
                    .willReturn(Optional.of(usedCoupon));
            given(couponRepository.findById(testCouponId))
                    .willReturn(Optional.of(testCoupon));

            // when & then
            assertThatThrownBy(() -> couponService.useCoupon(testUserId, testCouponId))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.COUPON_NOT_AVAILABLE);
        }
    }

    @Nested
    @DisplayName("사용자 쿠폰 목록 조회")
    class GetUserCoupons {
        @Test
        @DisplayName("사용자 쿠폰 목록 조회 성공")
        void getUserCouponsSuccess() {
            // given
            List<UserCoupon> userCoupons = List.of(testUserCoupon);
            given(couponRepository.findUserCoupons(testUserId))
                    .willReturn(userCoupons);
            given(couponRepository.findById(testCouponId))
                    .willReturn(Optional.of(testCoupon));

            // when
            List<CouponInfo.UserCouponInfo> results = couponService.getUserCoupons(testUserId);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).userCouponId()).isEqualTo(testCouponId);
            assertThat(results.get(0).discountPrice()).isEqualTo(5000L);
            verify(couponRepository).findUserCoupons(testUserId);
        }
    }
}