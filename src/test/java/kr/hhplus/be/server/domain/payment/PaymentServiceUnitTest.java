package kr.hhplus.be.server.domain.payment;

import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.constant.PaymentStatus;
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
@DisplayName("결제 서비스 테스트")
public class PaymentServiceUnitTest {
    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    private Payment testPayment;
    private PaymentCreate testPaymentCreate;

    @BeforeEach
    void setUp() {
        testPaymentCreate = new PaymentCreate(1L, 1L, 50000L);

        testPayment = Payment.createPayment(
                testPaymentCreate.orderId(),
                testPaymentCreate.userId(),
                testPaymentCreate.paymentAmount()
        );
    }

    @Nested
    @DisplayName("결제 생성")
    class CreatePayment {

        @Test
        @DisplayName("결제 생성 성공")
        void orderId_userId_총결제금액이_주어지면_결제데이터_생성() {
            // given
            given(paymentRepository.findByOrderId(testPaymentCreate.orderId()))
                    .willReturn(Optional.empty());
            given(paymentRepository.save(any(Payment.class)))
                    .willReturn(testPayment);

            // when
            Payment result = paymentService.createPayment(testPaymentCreate);

            // then
            assertThat(result.getOrderId()).isEqualTo(testPaymentCreate.orderId());
            assertThat(result.getUserId()).isEqualTo(testPaymentCreate.userId());
            assertThat(result.getPaymentAmount()).isEqualTo(testPaymentCreate.paymentAmount());
            assertThat(result.getStatus()).isEqualTo(PaymentStatus.PENDING);

            verify(paymentRepository).save(any(Payment.class));
        }

        @Test
        @DisplayName("중복 결제 요청 시 예외 발생")
        void 결제_상태가_PAID인_데이터에_중복_결제_요청시_DUPLICATE_PAYMENT_예외_전달() {
            // given
            given(paymentRepository.findByOrderId(testPaymentCreate.orderId()))
                    .willReturn(Optional.of(testPayment));

            // when & then
            assertThatThrownBy(() -> paymentService.createPayment(testPaymentCreate))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.DUPLICATE_PAYMENT.getMessage());
        }

        @Test
        @DisplayName("잘못된 결제 금액 전달 시 예외 발생")
        void 결제_금액으로_0_혹은_음수_전달시_INVALID_PAYMENT_AMOUNT_예외_전달() {
            // given
            PaymentCreate invalidCreate = new PaymentCreate(1L, 1L, 0L);

            // when & then
            assertThatThrownBy(() -> paymentService.createPayment(invalidCreate))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.INVALID_PAYMENT_AMOUNT.getMessage());
        }
    }

    @Nested
    @DisplayName("결제 상태 변경")
    class ChangePaymentStatus {

        @Test
        @DisplayName("결제 상태 완료 처리 성공")
        void 결제_성공_요청시_상태값을_PAID로_변경한다() {
            // given
            given(paymentRepository.findById(1L))
                    .willReturn(Optional.of(testPayment));
            given(paymentRepository.save(any(Payment.class)))
                    .willReturn(testPayment);

            // when
            Payment result = paymentService.completePayment(1L);

            // then
            assertThat(result.getStatus()).isEqualTo(PaymentStatus.PAID);
            verify(paymentRepository).save(any(Payment.class));
        }

        @Test
        @DisplayName("이미 완료된 결제 완료 처리 요청 시 예외 발생")
        void 결제_상태가_PAID인_결제에_완료_처리_요청시_PAYMENT_ALREADY_COMPLETED_예외_전달() {
            // given
            Payment paidPayment = Payment.createPayment(1L, 1L, 50000L);
            paidPayment.complete(); // 이미 완료 상태로 변경

            given(paymentRepository.findById(1L))
                    .willReturn(Optional.of(paidPayment));

            // when & then
            assertThatThrownBy(() -> paymentService.completePayment(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.PAYMENT_ALREADY_COMPLETED.getMessage());
        }

        @Test
        @DisplayName("결제 상태 실패 처리 성공")
        void 결제_실패_요청시_상태값을_CANCELLED로_변경한다() {
            // given
            given(paymentRepository.findById(1L))
                    .willReturn(Optional.of(testPayment));
            given(paymentRepository.save(any(Payment.class)))
                    .willReturn(testPayment);

            // when
            Payment result = paymentService.canclePayment(1L);

            // then
            assertThat(result.getStatus()).isEqualTo(PaymentStatus.CANCELLED);
            verify(paymentRepository).save(any(Payment.class));
        }

        @Test
        @DisplayName("이미 실패한 결제 실패 처리 요청 시 예외 발생")
        void 결제_상태가_CANCLED인_결제에_실패_처리_요청시_PAYMENT_CANCELLED_예외_전달() {
            // given
            Payment cancelledPayment = Payment.createPayment(1L, 1L, 50000L);
            cancelledPayment.cancel(); // 이미 취소 상태로 변경

            given(paymentRepository.findById(1L))
                    .willReturn(Optional.of(cancelledPayment));

            // when & then
            assertThatThrownBy(() -> paymentService.canclePayment(1L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.PAYMENT_CANCELLED.getMessage());
        }
    }

    @Nested
    @DisplayName("결제 조회")
    class GetPayment {

        @Test
        @DisplayName("성공: 결제 ID로 조회")
        void successGetById() {
            // given
            given(paymentRepository.findById(1L))
                    .willReturn(Optional.of(testPayment));

            // when
            Payment result = paymentService.getPayment(1L);

            // then
            assertThat(result.getPaymentId()).isEqualTo(testPayment.getPaymentId());
        }

        @Test
        @DisplayName("성공: 주문 ID로 조회")
        void successGetByOrderId() {
            // given
            given(paymentRepository.findByOrderId(1L))
                    .willReturn(Optional.of(testPayment));

            // when
            Payment result = paymentService.getPaymentByOrder(1L);

            // then
            assertThat(result.getOrderId()).isEqualTo(testPayment.getOrderId());
        }

        @Test
        @DisplayName("실패: 존재하지 않는 결제 조회")
        void failPaymentNotFound() {
            // given
            given(paymentRepository.findById(999L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> paymentService.getPayment(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.PAYMENT_NOT_FOUND.getMessage());
        }
    }
}
