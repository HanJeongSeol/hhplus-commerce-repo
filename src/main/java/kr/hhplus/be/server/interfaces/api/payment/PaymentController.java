package kr.hhplus.be.server.interfaces.api.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.dto.payment.PaymentDiscountResponse;
import kr.hhplus.be.server.interfaces.dto.payment.PaymentRequest;
import kr.hhplus.be.server.interfaces.dto.payment.PaymentResponse;
import kr.hhplus.be.server.support.constant.PaymentStatus;
import kr.hhplus.be.server.support.constant.SuccessCode;
import kr.hhplus.be.server.support.http.CustomApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "payments", description = "결제 API")
@RestController
@RequestMapping("/api/v1/payments")
public class PaymentController {


    @Operation(summary = "결제 처리", description = "주문에 대한 결제를 처리합니다.")
    @PostMapping
    public ResponseEntity<CustomApiResponse<PaymentResponse>> processPayment(
            @RequestBody PaymentRequest request) {

        // Mock 응답 데이터 생성 (실제 구현 시에는 Facade 호출)
        PaymentDiscountResponse discountInfo = new PaymentDiscountResponse(
                request.userCouponId(),
                "신규 가입 할인 쿠폰",
                5000L,
                LocalDateTime.now()
        );

        PaymentResponse response = new PaymentResponse(
                1L,
                request.orderId(),
                request.userId(),
                PaymentStatus.PAID,
                LocalDateTime.now(),
                50000L,
                discountInfo,
                45000L,
                105000L
        );

        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.PAYMENT_COMPLETED, response));
    }

    @Operation(summary = "결제 정보 조회", description = "결제 정보를 조회합니다.")
    @GetMapping("/{paymentId}")
    public ResponseEntity<CustomApiResponse<PaymentResponse>> getPayment(
            @Parameter(description = "결제 ID", required = true)
            @PathVariable Long paymentId) {

        // Mock 응답 데이터 생성 (실제 구현 시에는 Facade 호출)
        PaymentDiscountResponse discountInfo = new PaymentDiscountResponse(
                1L,
                "신규 가입 할인 쿠폰",
                5000L,
                LocalDateTime.now()
        );

        PaymentResponse response = new PaymentResponse(
                paymentId,
                1L,
                1L,
                PaymentStatus.PAID,
                LocalDateTime.now(),
                50000L,
                discountInfo,
                45000L,
                105000L
        );

        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.PAYMENT_FOUND, response));
    }
}
