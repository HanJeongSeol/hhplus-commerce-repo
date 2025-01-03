package kr.hhplus.be.server.domain.coupon;

import jakarta.persistence.*;
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
@Table(name = "coupon")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coupon {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long couponId;

    @OneToMany(mappedBy = "coupon")  // userCoupon 엔티티의 coupon 필드 참조
    private List<UserCoupon> userCoupons = new ArrayList<>();

    @Column(nullable = false)
    private String couponName;

    @Column(nullable = false)
    private Long discountAmount;

    @Column(nullable = false)
    private Long stock;

    @Column(nullable = false)
    private LocalDateTime expiredAt;

    @Enumerated(EnumType.STRING)
    private CouponStatus status;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 비즈니스 메서드
    public boolean isAvailable() {
        return !isExpired() && hasStock() && status == CouponStatus.AVAILABLE;
    }

    public void decreaseStock() {
        validateStockDecrease();
        this.stock--;
    }

    public boolean hasStock() {
        return this.stock > 0;
    }

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiredAt);
    }

    public Long calculateDiscountAmount(Long orderAmount) {
        return Math.min(orderAmount, this.discountAmount);
    }

    private void validateStockDecrease() {
        if (!hasStock()) {
            throw new IllegalArgumentException("쿠폰 재고가 부족합니다.");
        }
    }
}