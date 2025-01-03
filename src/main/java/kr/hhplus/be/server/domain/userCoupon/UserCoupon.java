package kr.hhplus.be.server.domain.userCoupon;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.coupon.Coupon;
import kr.hhplus.be.server.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "user_coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCoupon {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userCouponId;

    private Long couponId;
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "couponId",
            referencedColumnName = "couponId",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Coupon coupon;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "userId",
            referencedColumnName = "userId",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private User user;

    @Enumerated(EnumType.STRING)
    private UserCouponStatus status;

    private LocalDateTime usedAt;

    @CreatedDate
    private LocalDateTime createdAt;

    // 비즈니스 메서드
    public boolean isAvailable() {
        return status == UserCouponStatus.UNUSED
                && coupon != null
                && coupon.isAvailable();
    }

    public void use() {
        validateUse();
        this.status = UserCouponStatus.USED;
        this.usedAt = LocalDateTime.now();
    }

    public boolean isUsed() {
        return status == UserCouponStatus.USED;
    }

    private void validateUse() {
        if (!isAvailable()) {
            throw new IllegalStateException("사용할 수 없는 쿠폰입니다.");
        }
    }
}