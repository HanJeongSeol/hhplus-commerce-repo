package kr.hhplus.be.server.domain.dataPlatformLog;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.userCoupon.UserCoupon;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "data_platform_log")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DataPlatformLog {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long orderId;
    private Long paymentId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "orderId",
            referencedColumnName = "orderId",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "paymentId",
            referencedColumnName = "paymentId",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Payment payment;

    @Column(columnDefinition = "TEXT")
    private String orderDetail;

    @Column(columnDefinition = "TEXT")
    private String paymentDetail;

    @Column(columnDefinition = "TEXT")
    private String discountDetail;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 비즈니스 메서드
    public void setOrderInfo(Order order) {
        validateOrder(order);
        this.order = order;
        this.orderId = order.getOrderId();
        this.orderDetail = createOrderDetailJson(order);
    }

    public void setPaymentInfo(Payment payment) {
        validatePayment(payment);
        this.payment = payment;
        this.paymentId = payment.getPaymentId();
        this.paymentDetail = createPaymentDetailJson(payment);
    }

    public void setDiscountInfo(UserCoupon userCoupon) {
        validateUserCoupon(userCoupon);
        this.discountDetail = createDiscountDetailJson(userCoupon);
    }

    private void validateOrder(Order order) {
        if (order == null) {
            throw new IllegalArgumentException("주문 정보가 없습니다.");
        }
    }

    private void validatePayment(Payment payment) {
        if (payment == null) {
            throw new IllegalArgumentException("결제 정보가 없습니다.");
        }
    }

    private void validateUserCoupon(UserCoupon userCoupon) {
        if (userCoupon == null) {
            throw new IllegalArgumentException("쿠폰 정보가 없습니다.");
        }
    }

    private String createOrderDetailJson(Order order) {
        // Order 객체를 JSON 문자열로 변환하는 로직
        return "주문 상세 JSON";  // 실제 구현 필요
    }

    private String createPaymentDetailJson(Payment payment) {
        // Payment 객체를 JSON 문자열로 변환하는 로직
        return "결제 상세 JSON";  // 실제 구현 필요
    }

    private String createDiscountDetailJson(UserCoupon userCoupon) {
        // UserCoupon 객체를 JSON 문자열로 변환하는 로직
        return "할인 상세 JSON";  // 실제 구현 필요
    }
}