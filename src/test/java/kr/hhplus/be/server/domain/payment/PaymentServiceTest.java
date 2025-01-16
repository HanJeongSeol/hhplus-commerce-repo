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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("결제 서비스 테스트")
class PaymentServiceTest {

    @InjectMocks
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;


    private Payment testPayment;
    private Long testOrderId;
    private Long testUserId;
    private Long testTotalAmount;
    private Long testDiscountAmount;

    @BeforeEach
    void setUp() {
        testOrderId = 1L;
        testUserId = 1L;
        testTotalAmount = 50000L;
        testDiscountAmount = 5000L;

        testPayment = Payment.builder()
                .paymentId(1L)
                .orderId(testOrderId)
                .userId(testUserId)
                .paymentPrice(testTotalAmount - testDiscountAmount)
                .status(PaymentStatus.PENDING)
                .build();
    }

    @Nested
    @DisplayName("결제 생성")
    class CreatePayment {
        @Test
        @DisplayName("결제 생성 성공")
        void 할인이_적용된_결제_생성() {
            // given
            given(paymentRepository.save(any(Payment.class)))
                    .willReturn(testPayment);

            // when
            Payment result = paymentService.createPayment(
                    testOrderId,
                    testUserId,
                    testTotalAmount,
                    testDiscountAmount
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.getOrderId()).isEqualTo(testOrderId);
            assertThat(result.getUserId()).isEqualTo(testUserId);
            assertThat(result.getPaymentPrice()).isEqualTo(testTotalAmount - testDiscountAmount);
            assertThat(result.getStatus()).isEqualTo(PaymentStatus.PENDING);
            verify(paymentRepository).save(any(Payment.class));
        }
        @Test
        @DisplayName("할인이 없는 결제 생성 성공")
        void 할인이_없는_결제_생성() {
            // given
            Payment paymentWithoutDiscount = Payment.builder()
                    .paymentId(1L)
                    .orderId(testOrderId)
                    .userId(testUserId)
                    .paymentPrice(testTotalAmount)
                    .status(PaymentStatus.PENDING)
                    .build();

            given(paymentRepository.save(any(Payment.class)))
                    .willReturn(paymentWithoutDiscount);

            // when
            Payment result = paymentService.createPayment(
                    testOrderId,
                    testUserId,
                    testTotalAmount,
                    null
            );

            // then
            assertThat(result).isNotNull();
            assertThat(result.getPaymentPrice()).isEqualTo(testTotalAmount);
            verify(paymentRepository).save(any(Payment.class));
        }
    }

    @Nested
    @DisplayName("결제 승인")
    class ApprovePayment {
        @Test
        @DisplayName("결제 승인 성공")
        void 결제_승인_성공() {
            // given
            given(paymentRepository.findByIdWithLock(1L))
                    .willReturn(Optional.of(testPayment));
            given(paymentRepository.save(any(Payment.class)))
                    .willReturn(testPayment);

            // when
            Payment result = paymentService.approvePayment(1L);

            // then
            assertThat(result.getStatus()).isEqualTo(PaymentStatus.PAID);
            verify(paymentRepository).findByIdWithLock(1L);
            verify(paymentRepository).save(any(Payment.class));
        }

        @Test
        @DisplayName("존재하지 않는 결제 승인 시도시 예외 발생")
        void 존재하지_않는_결제_승인시_PAYMENT_NOT_FOUND_예외발생() {
            // given
            given(paymentRepository.findByIdWithLock(999L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> paymentService.approvePayment(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.PAYMENT_NOT_FOUND.getMessage());
        }
    }

//    @Nested
//    @DisplayName("결제 조회")
//    class GetPayment {
//        @Test
//        @DisplayName("결제 조회 성공")
//        void paymentId가_주어지면_결제정보를_조회() {
//            // given
//            given(paymentRepository.findById(1L))
//                    .willReturn(Optional.of(testPayment));
//
//            // when
//            Payment result = paymentService.getPayment(1L);
//
//            // then
//            assertThat(result).isNotNull();
//            assertThat(result.getPaymentId()).isEqualTo(1L);
//            verify(paymentRepository).findById(1L);
//        }
//
//        @Test
//        @DisplayName("존재하지 않는 결제 조회시 예외 발생")
//        void 존재하지_않는_paymentId가_주어지면_PAYMENT_NOT_FOUND_예외_발생() {
//            // given
//            given(paymentRepository.findById(999L))
//                    .willReturn(Optional.empty());
//
//            // when & then
//            assertThatThrownBy(() -> paymentService.getPayment(999L))
//                    .isInstanceOf(BusinessException.class)
//                    .hasMessage(ErrorCode.PAYMENT_NOT_FOUND.getMessage());
//        }
//    }
//
//    @Nested
//    @DisplayName("주문별 결제 조회")
//    class GetPaymentByOrder {
//        @Test
//        @DisplayName("주문별 결제 조회 성공")
//        void orderId가_주어지면_주문별_결제정보_조회() {
//            // given
//            given(paymentRepository.findByOrderId(testOrderId))
//                    .willReturn(Optional.of(testPayment));
//
//            // when
//            Payment result = paymentService.getPaymentByOrder(testOrderId);
//
//            // then
//            assertThat(result).isNotNull();
//            assertThat(result.getOrderId()).isEqualTo(testOrderId);
//            verify(paymentRepository).findByOrderId(testOrderId);
//        }
//    }

//    @Nested
//    @DisplayName("사용자별 결제 목록 조회")
//    class GetUserPayments {
//        @Test
//        @DisplayName("사용자별 결제 목록 조회 성공")
//        void userId가_주어지면_사용자별_결제목록_조회() {
//            // given
//            List<Payment> payments = List.of(testPayment);
//            given(paymentRepository.findByUserId(testUserId))
//                    .willReturn(payments);
//
//            // when
//            List<Payment> results = paymentService.getUserPayments(testUserId);
//
//            // then
//            assertThat(results).hasSize(1);
//            assertThat(results.get(0).getUserId()).isEqualTo(testUserId);
//            verify(paymentRepository).findByUserId(testUserId);
//        }
//
//        @Test
//        @DisplayName("사용자별 결제 목록 페이징 조회 성공")
//        void userId와_페이징정보가_주어지면_사용자별_결제목록_조회() {
//            // given
//            Pageable pageable = PageRequest.of(0, 10);
//            Page<Payment> pagedPayments = new PageImpl<>(List.of(testPayment));
//            given(paymentRepository.findByUserId(testUserId, pageable))
//                    .willReturn(pagedPayments);
//
//            // when
//            Page<Payment> results = paymentService.getUserPayments(testUserId, pageable);
//
//            // thenㄱ
//            assertThat(results.getContent()).hasSize(1);
//            assertThat(results.getContent().get(0).getUserId()).isEqualTo(testUserId);
//            verify(paymentRepository).findByUserId(testUserId, pageable);
//        }
//    }
}