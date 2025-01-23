package kr.hhplus.be.server.interfaces.api.payment;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.payment.PaymentFacade;
import kr.hhplus.be.server.application.payment.request.PaymentCommand;
import kr.hhplus.be.server.application.payment.response.PaymentResult;
import kr.hhplus.be.server.interfaces.dto.payment.request.PaymentRequest;
import kr.hhplus.be.server.interfaces.dto.payment.response.PaymentResponse;
import kr.hhplus.be.server.support.constant.PaymentStatus;
import kr.hhplus.be.server.support.constant.SuccessCode;
import kr.hhplus.be.server.support.http.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Tag(name = "payments", description = "결제 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/payments")
public class PaymentController {

    private final PaymentFacade paymentFacade;

    @Operation(summary = "결제 처리", description = "주문에 대한 결제를 처리합니다.")
    @PostMapping
    public ResponseEntity<CustomApiResponse<PaymentResponse.PaymentProcessingResponse>> processPayment(
            @RequestBody PaymentRequest.PaymentProcessingRequest request) {

        // Command 객체 생성
        PaymentCommand.ProcessPayment command = PaymentCommand.ProcessPayment.from(request);

        // Facade를 통한 결제 처리
        PaymentResult.PaymentProcessResult result = paymentFacade.processPayment(command);
        PaymentResponse.PaymentProcessingResponse response = PaymentResponse.PaymentProcessingResponse.from(result);

        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.PAYMENT_COMPLETED, response));
    }


    @Operation(summary = "결제 처리", description = "주문에 대한 결제를 처리합니다.")
    @PostMapping("/redis")
    public ResponseEntity<CustomApiResponse<PaymentResponse.PaymentProcessingResponse>> processPaymentByRedisAnnotation(
            @RequestBody PaymentRequest.PaymentProcessingRequest request) {

        // Command 객체 생성
        PaymentCommand.ProcessPayment command = PaymentCommand.ProcessPayment.from(request);

        // Facade를 통한 결제 처리
        PaymentResult.PaymentProcessResult result = paymentFacade.processPaymentByRedisAnnotation(command);
        PaymentResponse.PaymentProcessingResponse response = PaymentResponse.PaymentProcessingResponse.from(result);

        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.PAYMENT_COMPLETED, response));
    }

}
