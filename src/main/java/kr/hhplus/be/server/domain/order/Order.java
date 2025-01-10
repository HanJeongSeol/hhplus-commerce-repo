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


    @Column(nullable = false)
    @Comment("주문 총 금액")
    private Long totalPrice;

    @Enumerated(EnumType.STRING)
    @Comment("주문 상태")
    private OrderStatus status;

    /**
     * Order 객체 생성
     */
    public static Order createOrder(Long userId){
        return Order.builder()
                .userId(userId)
                .status(OrderStatus.PENDING)
                .totalPrice(0L)
                .build();
    }

    /**
     * 주문 상세 추가 & 전체 금액 반영
     */
    public void addOrderDetail(OrderDetail orderDetail) {
        orderDetail.assignOrder(this.orderId);
        updateTotalPrice(orderDetail.getTotalPrice());
    }

    /**
     * 총 주문 금액
     */
    public void updateTotalPrice(long detailPrice) {
        this.totalPrice = (this.totalPrice == null ? 0 : this.totalPrice) + detailPrice;
    }

    /**
     * 주문 완료
     */
    public void complete() {
        validateCompletable();
        this.status = OrderStatus.COMPLETED;
    }

    /**
     * 주문 취소
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
