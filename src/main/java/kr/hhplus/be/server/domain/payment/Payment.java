package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long paymentId;

    private Long orderId;
    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
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
            name = "userId",
            referencedColumnName = "userId",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private User user;

    @Column(nullable = false)
    private Long paymentAmount;

    @Enumerated(EnumType.STRING)
    private PaymentStatus status;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 비즈니스 메서드
    public void complete() {
        validatePaymentComplete();
        this.status = PaymentStatus.COMPLETED;
    }

    public void fail() {
        validatePaymentFail();
        this.status = PaymentStatus.CANCELLED;
    }

    public boolean isCompleted() {
        return this.status == PaymentStatus.COMPLETED;
    }

    public void validatePayment() {
        if (this.paymentAmount <= 0) {
            throw new IllegalArgumentException("결제 금액은 0보다 커야 합니다.");
        }
        if (this.order == null) {
            throw new IllegalStateException("주문 정보가 없습니다.");
        }
        if (!this.paymentAmount.equals(order.getPaymentAmount())) {
            throw new IllegalStateException("결제 금액이 일치하지 않습니다.");
        }
    }

    private void validatePaymentComplete() {
        if (isCompleted()) {
            throw new IllegalStateException("이미 완료된 결제입니다.");
        }
    }

    private void validatePaymentFail() {
        if (isCompleted()) {
            throw new IllegalStateException("이미 완료된 결제는 실패 처리할 수 없습니다.");
        }
    }
}