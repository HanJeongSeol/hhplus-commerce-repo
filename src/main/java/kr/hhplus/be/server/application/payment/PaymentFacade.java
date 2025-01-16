package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.payment.request.PaymentCommand;
import kr.hhplus.be.server.application.payment.response.PaymentResult;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.OrderService;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.payment.PaymentService;
import kr.hhplus.be.server.domain.payment.dto.PaymentInfo;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.product.ProductService;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.constant.OrderStatus;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PaymentFacade {
    private final PaymentService paymentService;
    private final OrderService orderService;
    private final PointService pointService;
    private final ProductService productService;
    private final CouponService couponService;

    @Transactional
    public PaymentResult.PaymentProcessResult processPayment(PaymentCommand.ProcessPayment command) {
        // 1. 주문 정보 조회 및 검증
        var order = orderService.getOrder(command.orderId());
        if(OrderStatus.COMPLETED.equals(order.getStatus())){
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_COMPLETED);
        }
        // 2. 주문 상세 정보 조회
        var orderLine = orderService.getOrderLine(command.orderId());

        // 3. 재고 조회 후 재고 감소
        orderLine.forEach(line ->{
            // 3-2 상품 재고 감소
            productService.decreaseProductStock(line.getProductId(), line.getQuantity());
        });

        // 4. 쿠폰 조회 및 검증
        var coupon = command.userCouponId() != null ?
                couponService.getCoupon(command.userCouponId()) : null;
        if (command.userCouponId() != null) {
            couponService.userCouponCheck(command.userId(), command.userCouponId());
        }

        // 5. 결제 생성
        Payment payment = paymentService.createPayment(
                command.orderId(),
                command.userId(),
                order.getTotalPrice(),
                coupon != null ? coupon.getDiscountPrice() : null
        );
        // 6. 포인트 차감
        var point = pointService.usePoint(command.userId(), payment.getPaymentPrice());

        // 7. 쿠폰 사용
        if (coupon != null) {
            couponService.useCoupon(command.userId(), command.userCouponId());
        }

        // 8. 결제 승인
        Payment approvedPayment = paymentService.approvePayment(payment.getPaymentId());

        // 9. 주문 완료
        orderService.completeOrder(command.orderId());

        // 10. 결과 반환
        var discountInfo = coupon != null ? new PaymentResult.DiscountInfo(
                coupon.getCouponId(),
                coupon.getName(),
                coupon.getDiscountPrice(),
                LocalDateTime.now()
        ) : null;

        return PaymentResult.PaymentProcessResult.of(
                approvedPayment,
                order.getTotalPrice(),
                discountInfo,
                payment.getPaymentPrice(),
                point.getBalance()
        );

    }
}
