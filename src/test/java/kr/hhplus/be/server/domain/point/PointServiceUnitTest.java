package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.config.TestUtil;
import kr.hhplus.be.server.domain.user.User;
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

import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.Assert.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("포인트 서비스 테스트")
public class PointServiceUnitTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private PointRepository pointRepository;

    private User user;
    private Point point;

    @BeforeEach
    void init() {
        // 사용자 및 초기 포인트 설정
        user = TestUtil.createTestUser();
        point = user.getPoint();
    }

    @Nested
    @DisplayName("포인트 충전 기능")
    class ChargePointTest {
        @Test
        @DisplayName("포인트 충전 성공")
        void 사용자의_1000포인트_충전_요청에_성공하면_2_000L_포인트를_반환한다() {

            // given
            Long chargeAmount = 1_000L;

            given(pointRepository.findByUserWithLock(user)).willReturn(Optional.of(point));
            given(pointRepository.save(point)).willReturn(point);

            // when
            Point updatedPoint = pointService.chargePoint(user, chargeAmount);

            // then
            assertThat(updatedPoint.getBalance()).isEqualTo(point.getBalance());
            assertThat(updatedPoint.getBalance()).isEqualTo(2000L);
        }

        @Test
        @DisplayName("포인트 충전 후 최대 보유 포인트에 정확히 도달할 경우 성공한다")
        void 충전_후_최대_포인트_한도_도달시_정상처리() {
            // given
            point.charge(9_989_000L);  // 1_000L 보유 중 + 9_990_000L = 10_000_000L
            Long chargeAmount = 10_000L;

            given(pointRepository.findByUserWithLock(user)).willReturn(Optional.of(point));
            given(pointRepository.save(point)).willReturn(point);

            // when
            Point updatedPoint = pointService.chargePoint(user, chargeAmount);

            // then
            assertThat(updatedPoint.getBalance()).isEqualTo(10_000_000L);
        }

        @ParameterizedTest
        @ValueSource(longs = {0L, -1L, -100L, -1000L})
        @DisplayName("잘못된 포인트 금액이 들어올 시 예외 발생")
        void 포인트_충전_금액으로_0_혹은_음수가_입력된다면_INVALID_POINT_AMOUNT_예외_발생(Long chargeAmount) {
            // given
            given(pointRepository.findByUserWithLock(user)).willReturn(Optional.of(point));

            // when & then\
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> pointService.chargePoint(user, chargeAmount));

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_POINT_AMOUNT);

        }

        // 9940000
        @Test
        @DisplayName("충전 후 금액이 최대 보유 포인트를 초과할 시 예외 발생(MAX=10_000_000L)")
        void _50_000_포인트_충전_후_10_011_000L_포인트로_최대_보유_포인트_초과하여_POINT_EXCEED_MAX_VALUE_EXCEPTION_예외_발생() {
            // given
            point.charge(9_961_000L);   // 현재 포인트 설정
            Long chargeAmount = 50_000L;

            given(pointRepository.findByUserWithLock(user)).willReturn(Optional.of(point));

            // when & then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> pointService.chargePoint(user, chargeAmount));

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.POINT_EXCEED_MAX_VALUE);
        }


    }

    @Nested
    @DisplayName("포인트 조회 테스트")
    class getUserPointTest {

        @Test
        @DisplayName("포인트 조회 성공")
        void 조회_요청에_성공하면_1_000L_포인트를_반환한다() {
            // given
            given(pointRepository.findByUserWithLock(user)).willReturn(Optional.of(point));

            // when
            Point foundPoint = pointService.getPoint(user);

            // then
            assertThat(foundPoint).isNotNull();
            assertThat(foundPoint.getBalance()).isEqualTo(point.getBalance());

        }

        @Test
        @DisplayName("사용자 포인트 정보가 없을 시 예외를 발생한다")
        void 사용자_포인트_정보가_존재하지_않을_경우_USER_POINT_NOT_FOUND_EXCEPTION_예외_발생() {
            // given
            given(pointRepository.findByUserWithLock(user)).willReturn(Optional.empty());

            // when & then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> pointService.getPoint(user));

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.USER_POINT_NOT_FOUND);
        }
    }

    @Nested
    @DisplayName("포인트 사용 테스트")
    class usePointTest {
        @Test
        @DisplayName("포인트 사용 성공")
        void 사용자의_5_000L_포인트_사용_성공시_45_000L_포인트를_반환한다() {
            // given
            point.charge(49_000L);  // 기본 제공 1000 + 49000 = 50000
            Long useAmount = 5_000L;

            given(pointRepository.findByUserWithLock(user)).willReturn(Optional.of(point));
            given(pointRepository.save(point)).willReturn(point);

            // when
            Point usePoint = pointService.usePoint(user, useAmount);

            // then
            assertThat(usePoint.getBalance()).isEqualTo(point.getBalance());
            assertThat(usePoint.getBalance()).isEqualTo(45_000L);

        }

        @Test
        @DisplayName("포인트 사용 후 잔액이 정확히 0이 되는 경우 성공한다")
        void 포인트_사용_후_잔액이_정확히_0이_되는_경우_정상처리() {
            // given
            point.charge(9_000L);  // 1_000L(기본제공) + 9_000L = 10_000L
            Long useAmount = 10_000L;

            given(pointRepository.findByUserWithLock(user)).willReturn(Optional.of(point));
            given(pointRepository.save(point)).willReturn(point);

            // when
            Point usePoint = pointService.usePoint(user, useAmount);

            // then
            assertThat(usePoint.getBalance()).isEqualTo(0L);
        }


        @ParameterizedTest
        @ValueSource(longs = {0L, -1L, -100L, -1000L})
        @DisplayName("잘못된 포인트 금액이 들어올 시 예외 발생")
        void 포인트_사용_금액으로_0_혹은_음수가_입력된다면_INVALID_POINT_AMOUNT_예외_발생(Long useAmount) {
            // given
            given(pointRepository.findByUserWithLock(user)).willReturn(Optional.of(point));

            // when & then\
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> pointService.usePoint(user, useAmount));

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INVALID_POINT_AMOUNT);
        }

        @Test
        @DisplayName("사용 포인트가 보유 포인트를 초과하는 경우 예외를 발생한다")
        void 보유_포인트_50_000L이고_사용_포인트_100_000L_요청시_INSUFFICIENT_POINT_BALANCE_EXCEPTION_예외_발생(){
            // given
            point.charge(49_000L);
            Long useAmount = 100_000L;

            given(pointRepository.findByUserWithLock(user)).willReturn(Optional.of(point));

            // when & then
            BusinessException exception = assertThrows(BusinessException.class,
                    () -> pointService.usePoint(user, useAmount));

            assertThat(exception.getErrorCode()).isEqualTo(ErrorCode.INSUFFICIENT_POINT_BALANCE);
        }
    }
    @Test
    @DisplayName("포인트 충전 시 findByUserWithLock이 호출되어 락이 걸리는지 검증")
    void 포인트_충전시_DB_락_검증() {
        // given
        Long chargeAmount = 1_000L;

        given(pointRepository.findByUserWithLock(user)).willReturn(Optional.of(point));
        given(pointRepository.save(point)).willReturn(point);

        // when
        pointService.chargePoint(user, chargeAmount);

        // then
        // findByUser가 호출되었는지 검증
        verify(pointRepository, times(1)).findByUserWithLock(user);

        assertThat(point.getBalance()).isEqualTo(2_000L);
    }

}
