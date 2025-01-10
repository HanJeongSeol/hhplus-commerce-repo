package kr.hhplus.be.server.domain.payment;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.constant.PaymentStatus;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "payment")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Payment extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="payment_id")
    @Comment("결제 식별자")
    private Long paymentId;

    @Column(name="order_id", nullable = false)
    @Comment("주문 식별자")
    private Long orderId;

    @Column(name="user_id", nullable = false)
    @Comment("사용자 식별자")
    private Long userId;


    @Column(nullable = false)
    @Comment("결제 금액")
    private Long paymentPrice;

    @Enumerated(EnumType.STRING)
    @Comment("결제 상태")
    private PaymentStatus status;

    /**
     * 결제 승인
     */
    public void approve(){
        if(this.status == PaymentStatus.CANCELLED){
            throw new BusinessException(ErrorCode.PAYMENT_CANCELLED);
        }
        if(this.status == PaymentStatus.PAID){
            throw new BusinessException(ErrorCode.PAYMENT_ALREADY_COMPLETED);
        }
        this.status = PaymentStatus.PAID;
    }
}
