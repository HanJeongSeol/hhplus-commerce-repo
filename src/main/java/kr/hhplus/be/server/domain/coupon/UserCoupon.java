package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.support.constant.CouponStatus;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupons")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class UserCoupon extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_coupon_id")
    @Comment("사용자 쿠폰 식별자")
    private Long userCouponId;

    @Column(name="coupon_id", nullable = false)
    @Comment("쿠폰 식별자")
    private Long couponId;

    @Column(name="user_id", nullable = false)
    @Comment("사용자 식별자")
    private Long userId;

    @Column
    @Comment("쿠폰 사용 일시")
    private LocalDateTime usedAt;

    @Enumerated(EnumType.STRING)
    @Comment("사용자 쿠폰 상태")
    private CouponStatus status;

    /**
     * 쿠폰 사용
     * 1. 사용 가능 여부 검증
     * 2. 상태 변경 및 사용 시간 기록
     */

    public void use(Coupon coupon){
        validateUsable(coupon);
        updateUsageInfo();
    }

    /**
     * 사용 가능 상태 반환
     */
    public boolean isAvailable(Coupon coupon) {
        return this.status == CouponStatus.ACTIVE && !coupon.isExpired();
    }

    /**
     * 쿠폰 사용 or 만료 상태 확인
     */
    private void validateUsable(Coupon coupon){
        if(this.status == CouponStatus.USED){
            throw new BusinessException(ErrorCode.COUPON_NOT_AVAILABLE);
        }
        if (coupon.isExpired()) {
            this.status = CouponStatus.EXPIRED;
            throw new BusinessException(ErrorCode.COUPON_EXPIRED);
        }
    }


    private void updateUsageInfo(){
        this.status = CouponStatus.USED;
        this.usedAt = LocalDateTime.now();
    }



}
