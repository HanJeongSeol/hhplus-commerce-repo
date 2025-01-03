package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.orderDetail.OrderDetail;
import kr.hhplus.be.server.domain.payment.Payment;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.userCoupon.UserCoupon;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Order {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderId;

    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "userId",
            referencedColumnName = "userId",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private User user;

    @Column(nullable = false)
    private Long totalAmount;

    private Long discountAmount;

    @Column(nullable = false)
    private Long paymentAmount;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @OneToMany(mappedBy = "order")
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @OneToOne(mappedBy = "order")
    private Payment payment;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 비즈니스 메서드
    public void calculateTotalAmount() {
        this.totalAmount = orderDetails.stream()
                .mapToLong(OrderDetail::getTotalAmount)
                .sum();
        this.paymentAmount = this.totalAmount - (this.discountAmount != null ? this.discountAmount : 0);
    }

    public void applyDiscount(UserCoupon userCoupon) {
        validateDiscount(userCoupon);
        this.discountAmount = userCoupon.getCoupon().calculateDiscountAmount(this.totalAmount);
        calculateTotalAmount();
    }

    public void validateOrder() {
        if (orderDetails.isEmpty()) {
            throw new IllegalStateException("주문 상품이 없습니다.");
        }
        orderDetails.forEach(OrderDetail::validateStock);
    }

    public void complete() {
        validateOrderComplete();
        this.status = OrderStatus.COMPLETED;
    }

    public void cancel() {
        validateOrderCancel();
        this.status = OrderStatus.CANCELLED;
    }

    public boolean isCompleted() {
        return this.status == OrderStatus.COMPLETED;
    }

    private void validateDiscount(UserCoupon userCoupon) {
        if (!userCoupon.isAvailable()) {
            throw new IllegalArgumentException("사용할 수 없는 쿠폰입니다.");
        }
    }

    private void validateOrderComplete() {
        if (isCompleted()) {
            throw new IllegalStateException("이미 완료된 주문입니다.");
        }
    }

    private void validateOrderCancel() {
        if (isCompleted()) {
            throw new IllegalStateException("완료된 주문은 취소할 수 없습니다.");
        }
    }
}