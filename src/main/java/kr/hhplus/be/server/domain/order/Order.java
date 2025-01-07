package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.constant.OrderStatus;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "order_main")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Order extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_id")
    @Comment("주문 식별자")
    private Long orderId;

    @Column(name="user_id",nullable = false)
    @Comment("사용자 식별자")
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            referencedColumnName = "user_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private User user;

    @Column(nullable = false)
    @Comment("주문 총 금액")
    private Long totalAmount;

    @Enumerated(EnumType.STRING)
    @Comment("주문 상태")
    private OrderStatus status;

    @OneToMany(mappedBy = "order")
    // 주문 상세 내역
    private List<OrderDetail> orderDetails;


    // 팩토리 메서드로 Order 생성
    public static Order createOrder(Long userId) {
        return Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .orderDetails(new ArrayList<>())
                .build();
    }

    public void addOrderDetail(OrderDetail orderDetail) {
        this.orderDetails.add(orderDetail);
        orderDetail.assignOrder(this);
    }


    /**
     * 총 주문 금액
     * 1. 사용자가 OrderDetails 리스트 객체를 전달.
     * 2. Order 기본 객체 생성
     * 3. order.add(OrderDetails[0~1]) -> 리스트에 저장된 데이터 저장 .
     * 4. order.
     */
    public long getTotalAmount() {
        return this.orderDetails.stream()
                .mapToLong(OrderDetail::getTotalAmount)
                .sum();
    }


    /**
     * 주문 완료
     * 1. 완료 가능 상태 검증
     * 2. 주문 상태 COMPLETED 변경
     */
    public void complete() {
        validateCompletable();
        this.status = OrderStatus.COMPLETED;
    }

    /**
     * 주문 취소
     * 1. 취소 가능 상태 검증
     * 2. 주문 상태 CANCELLED 변경
     */
    public void cancel() {
        validateCancellable();
        this.status = OrderStatus.CANCELLED;
    }

    private void validateCompletable() {
        if (this.status != OrderStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_STATUS);
        }
    }

    private void validateCancellable() {
        if (this.status != OrderStatus.PENDING) {
            throw new BusinessException(ErrorCode.INVALID_ORDER_STATUS);
        }
    }

}
