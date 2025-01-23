package kr.hhplus.be.server.application.payment;

import kr.hhplus.be.server.application.payment.request.PaymentCommand;
import kr.hhplus.be.server.application.payment.response.PaymentResult;
import kr.hhplus.be.server.config.redis.annotation.RedissonLock;
import kr.hhplus.be.server.domain.coupon.CouponService;
import kr.hhplus.be.server.domain.order.OrderLine;
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
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
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
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_COMPLETED, command.orderId());
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

    @Transactional
    public PaymentResult.PaymentProcessResult processPaymentByRedis(PaymentCommand.ProcessPayment command) {
        // 1. 주문 정보 조회 및 검증
        var order = orderService.getOrder(command.orderId());
        if(OrderStatus.COMPLETED.equals(order.getStatus())){
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_COMPLETED, command.orderId());
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

//    @Transactional(propagation = Propagation.REQUIRED)
//    public PaymentResult.PaymentProcessResult processPaymentByRedisAnnotation(PaymentCommand.ProcessPayment command) {
//        log.info("결제 파사드 주문 아이디 : {} ", command.orderId());
//        // 1. 주문 정보 조회 및 검증
//        var order = orderService.getOrder(command.orderId());
//        if(OrderStatus.COMPLETED.equals(order.getStatus())){
//            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_COMPLETED, command.orderId());
//        }
//        // 2. 주문 상세 정보 조회
//        var orderLine = orderService.getOrderLine(command.orderId());
//
//        // 3. 재고 조회 후 재고 감소
//        orderLine.forEach(line ->{
//            // 3-2 상품 재고 감소
//            productService.decreaseProductStockRedisByAnnotation(line.getProductId(), line.getQuantity());
//            log.info("감소 후 재고 : {} ", productService.getProductById(line.getProductId()).stock() );
//        });
//
//        // 4. 쿠폰 조회 및 검증
//        var coupon = command.userCouponId() != null ?
//                couponService.getCoupon(command.userCouponId()) : null;
//        if (command.userCouponId() != null) {
//            couponService.userCouponCheck(command.userId(), command.userCouponId());
//        }
//
//        // 5. 결제 생성
//        Payment payment = paymentService.createPayment(
//                command.orderId(),
//                command.userId(),
//                order.getTotalPrice(),
//                coupon != null ? coupon.getDiscountPrice() : null
//        );
//        // 6. 포인트 차감
//        var point = pointService.usePoint(command.userId(), payment.getPaymentPrice());
//
//        // 7. 쿠폰 사용
//        if (coupon != null) {
//            couponService.useCoupon(command.userId(), command.userCouponId());
//        }
//
//        // 8. 결제 승인
//        Payment approvedPayment = paymentService.approvePayment(payment.getPaymentId());
//
//        // 9. 주문 완료
//        orderService.completeOrder(command.orderId());
//
//        // 10. 결과 반환
//        var discountInfo = coupon != null ? new PaymentResult.DiscountInfo(
//                coupon.getCouponId(),
//                coupon.getName(),
//                coupon.getDiscountPrice(),
//                LocalDateTime.now()
//        ) : null;
//
//        return PaymentResult.PaymentProcessResult.of(
//                approvedPayment,
//                order.getTotalPrice(),
//                discountInfo,
//                payment.getPaymentPrice(),
//                point.getBalance()
//        );
//
//    }

    @Transactional(propagation = Propagation.REQUIRED)
    public PaymentResult.PaymentProcessResult processPaymentByRedisAnnotation(PaymentCommand.ProcessPayment command) {
        log.info("결제 파사드 주문 아이디 : {} ", command.orderId());

        // 1. 주문 정보 조회 및 검증
        var order = orderService.getOrder(command.orderId());
        if(OrderStatus.COMPLETED.equals(order.getStatus())){
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_COMPLETED, command.orderId());
        }

        // 2. 주문 상세 정보 조회
        var orderLines = orderService.getOrderLine(command.orderId());

        // [A] --------------------- 재고 차감 (독립 트랜잭션) ----------------------
        try {
            List<OrderLine> sortedOrderLines = orderLines.stream()
                    .sorted(Comparator.comparing(OrderLine::getProductId)) // productId 오름차순 정렬
                    .collect(Collectors.toList());
            // 3. 재고 조회 후 재고 감소 (REQUIRES_NEW로 즉시 커밋)
            for (OrderLine line : sortedOrderLines) {
                productService.decreaseProductStockRedisByAnnotation(line.getProductId(), line.getQuantity());
                log.info("감소 후 재고 : {} ",
                        productService.getProductById(line.getProductId()).stock());
            }

        } catch (Exception e) {
            // 재고 차감 자체가 실패하면 여기서 바로 Exception 처리
            log.error("재고 차감 중 예외 발생!", e);
            throw new BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK, e);
        }

        // [B] --------------------- 결제/포인트/쿠폰/주문 처리 ---------------------
        Payment payment = null;
        Payment approvedPayment = null;
        PaymentResult.DiscountInfo discountInfo = null;
        Long pointBalance = null;

        try {
            // 4. 쿠폰 조회 및 검증
            var coupon = (command.userCouponId() != null)
                    ? couponService.getCoupon(command.userCouponId())
                    : null;
            if (coupon != null) {
                couponService.userCouponCheck(command.userId(), command.userCouponId());
            }

            // 5. 결제 생성
            payment = paymentService.createPayment(
                    command.orderId(),
                    command.userId(),
                    order.getTotalPrice(),
                    coupon != null ? coupon.getDiscountPrice() : null
            );

            // 6. 포인트 차감
            var point = pointService.usePoint(command.userId(), payment.getPaymentPrice());
            pointBalance = point.getBalance();

            // 7. 쿠폰 사용
            if (coupon != null) {
                couponService.useCoupon(command.userId(), command.userCouponId());
                discountInfo = new PaymentResult.DiscountInfo(
                        coupon.getCouponId(),
                        coupon.getName(),
                        coupon.getDiscountPrice(),
                        LocalDateTime.now()
                );
            }

            // 8. 결제 승인
            approvedPayment = paymentService.approvePayment(payment.getPaymentId());

            // 9. 주문 완료
            orderService.completeOrder(command.orderId());

        } catch (Exception e) {
            // ***** [C] 결제/포인트/쿠폰 처리 중 예외 발생 시, 재고 복원 *****
            log.error("결제/포인트/쿠폰 처리 중 예외 발생! 재고 복원을 시도합니다.", e);

            // 이미 차감된 재고를 보상 트랜잭션(restoreStockNewTx)으로 복원
            for (OrderLine line : orderLines) {
                try {
                    productService.restoreStockNewTx(line.getProductId(), line.getQuantity());
                } catch (Exception restoreEx) {
                    log.error("[재고 복원 실패] productId={}, quantity={}", line.getProductId(), line.getQuantity(), restoreEx);
                }
            }

            // 이후 로직 필요 시 추가 (결제 취소, 포인트 환불 등등)
            // throw 하거나, 적절한 결과 반환
            throw e;  // 혹은 throw new BusinessException(ErrorCode.PAYMENT_PROCESS_FAILED, e);
        }

        // [D] --------------------- 정상 완료 시 결과 반환 ---------------------
        return PaymentResult.PaymentProcessResult.of(
                approvedPayment,              // 승인된 결제 객체
                order.getTotalPrice(),        // 주문 총 금액
                discountInfo,                 // 쿠폰 할인 정보
                approvedPayment.getPaymentPrice(),
                pointBalance                  // 최종 포인트 잔액
        );
    }
}
