package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Entity
@Table(name = "user")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class User extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="user_id")
    @Comment("사용자 식별자")
    private Long userId;

//    @Column(name="point_id", nullable = false)
//    @Comment("포인트 식별자")
//    private Long pointId;

    @Column(nullable = false)
    @Comment("사용자 이름")
    private String name;

    // Point 엔티티와의 연관 관계 (조회 전용)
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name="point_id" ,   // User 테이블의 point_id
            referencedColumnName = "point_id",   // Point 테이블의 point_id 참조
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Point point;

    // 사용자 보유 쿠폰 리스트
    @OneToMany(mappedBy = "user")
    private List<UserCoupon> userCoupons = new ArrayList<>();

    // 사용자 주문 내역 리스트
    @OneToMany(mappedBy = "user")
    private List<Order> orders = new ArrayList<>();

    /**
     * 사용자 포인트 정보 존재 여부 확인
     * - 포인트 정보 존재 여부 확인
     */
    public void validateUser() {
        if (this.point == null) {
            throw new BusinessException(ErrorCode.USER_POINT_NOT_FOUND);
        }
    }

    /**
     * 사용자 보유 포인트 조회
     */
    public Long getBalance(){
        validateUser();
        return this.point.getBalance();
    }

    /**
     * 사용자 사용 가능한 쿠폰 목록 조회
     * - 만료되지 않은 미사용 쿠폰 필터링하여 반환
     */
    public List<UserCoupon> getAvailableCoupons(){
        return this.userCoupons.stream()
                .filter(this::isAvailableCoupon)
                .collect(Collectors.toList());
    }

    /**
     * 사용자 보유 쿠폰 중 특정 쿠폰의 사용 가능 여부 확인
     */
    public boolean hasAvailableCoupon(Long couponId) {
        return this.userCoupons.stream()
                .anyMatch(userCoupon ->
                        userCoupon.getCouponId().equals(couponId) &&
                                isAvailableCoupon(userCoupon)
                );
    }

    /**
     * 쿠폰 사용 가능 여부 확인
     * - 미사용 상태
     * - 만료되지 않은 쿠폰
     */
    private boolean isAvailableCoupon(UserCoupon userCoupon) {
        return userCoupon.isAvailable();
    }


    /**
     * 주문 내역 추가
     */
    public void addOrder(Order order) {
        this.orders.add(order);
    }

    /**
     * 쿠폰 추가
     */
    public void addUserCoupon(UserCoupon userCoupon) {
        this.userCoupons.add(userCoupon);
    }
}
