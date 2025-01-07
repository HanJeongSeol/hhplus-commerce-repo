package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.support.constant.CouponStatus;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "coupon")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Coupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="coupon_id")
    @Comment("쿠폰 식별자")
    private Long couponId;

    @Column(nullable = false)
    @Comment("쿠폰 이름")
    private String name;

    @Column(nullable = false)
    @Comment("할인 금액")
    private Long discountAmount;

    @Column(nullable = false)
    @Comment("쿠폰 재고")
    private int stock;

    @Column(nullable = false)
    @Comment("쿠폰 만료 시간")
    private LocalDateTime expiredAt;

    @Enumerated(EnumType.STRING)
    @Comment("쿠폰 상태")
    private CouponStatus status;

    /**
     * 쿠폰 발급
     * 1. 쿠폰 유효성 검사
     * 2. 재고 감소
     * 3. 상태 업데이트
     */
    public void issue(){
        validateIssuable();
        this.stock--;
        updateStatus();
    }

    private void validateIssuable(){
        if(this.stock <= 0){
            throw new BusinessException(ErrorCode.COUPON_OUT_OF_STOCK);
        }
        if(LocalDateTime.now().isAfter(this.expiredAt)){
            this.status = CouponStatus.INVALID;
            throw new BusinessException(ErrorCode.COUPON_EXPIRED);
        }
    }
    private void updateStatus(){
        if(this.stock <= 0){
            this.status = CouponStatus.INVALID;
        }
    }

    public boolean isExpired(){
        return this.status == CouponStatus.INVALID;
    }
}
