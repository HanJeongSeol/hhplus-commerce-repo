package kr.hhplus.be.server.domain.point;

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

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("포인트 서비스 테스트")
class PointServiceTest {

    @InjectMocks
    private PointService pointService;

    @Mock
    private PointRepository pointRepository;

    private Point testPoint;

    @BeforeEach
    void setUp() {
        testPoint = Point.builder()
                .pointId(1L)
                .userId(1L)
                .balance(1000L)
                .build();
    }

    @Nested
    @DisplayName("포인트 충전")
    class ChargePoint {
        @Test
        @DisplayName("포인트 충전 성공")
        void userId와_chargeAmount가_주어지면_포인트_충전_성공() {
            // given
            Long userId = 1L;
            Long chargeAmount = 10000L;
            given(pointRepository.findByUserWithLock(userId))
                .willReturn(Optional.of(testPoint));
            given(pointRepository.save(any(Point.class)))
                .willReturn(testPoint);

            // when
            Point result = pointService.chargePoint(userId, chargeAmount);

            // then
            assertThat(result.getBalance()).isEqualTo(11000L);
            verify(pointRepository).save(any(Point.class));
        }
    }

    @Nested
    @DisplayName("포인트 사용")
    class UsePoint {
        @Test
        @DisplayName("포인트 사용 성공")
        void userId와_useAmount가_주어지면_포인트_사용_성공() {
            // given
            Long userId = 1L;
            Long useAmount = 500L;
            given(pointRepository.findByUserWithLock(userId))
                .willReturn(Optional.of(testPoint));
            given(pointRepository.save(any(Point.class)))
                .willReturn(testPoint);

            // when
            Point result = pointService.usePoint(userId, useAmount);

            // then
            assertThat(result.getBalance()).isEqualTo(500L);
            verify(pointRepository).save(any(Point.class));
        }

        @Test
        @DisplayName("잔액 부족시 예외 발생")
        void 잔액이_부족한_상태에서_useAmount가_주어지면_INSUFFICIENT_POINT_BALANCE_예외_발생() {
            // given
            Long userId = 1L;
            Long useAmount = 2000L;
            given(pointRepository.findByUserWithLock(userId))
                .willReturn(Optional.of(testPoint));

            // when & then
            assertThatThrownBy(() -> pointService.usePoint(userId, useAmount))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.INSUFFICIENT_POINT_BALANCE.getMessage());
        }
    }

    @Nested
    @DisplayName("포인트 조회")
    class GetPoint {
        @Test
        @DisplayName("포인트 조회 성공")
        void userId가_주어지면_포인트_조회_성공() {
            // given
            Long userId = 1L;
            given(pointRepository.findByUserWithLock(userId))
                .willReturn(Optional.of(testPoint));

            // when
            Point result = pointService.getPoint(userId);

            // then
            assertThat(result).isNotNull();
            assertThat(result.getBalance()).isEqualTo(1000L);
        }

        @Test
        @DisplayName("존재하지 않는 사용자 포인트 조회시 예외 발생")
        void  존재하지_않는_userId가_주어지면_USER_POINT_NOT_FOUND_예외_발생() {
            // given
            Long userId = 999L;
            given(pointRepository.findByUserWithLock(userId))
                .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> pointService.getPoint(userId))
                .isInstanceOf(BusinessException.class)
                .hasMessage(ErrorCode.USER_POINT_NOT_FOUND.getMessage());
        }
    }
} 