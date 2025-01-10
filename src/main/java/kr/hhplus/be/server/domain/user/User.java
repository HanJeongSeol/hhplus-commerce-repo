package kr.hhplus.be.server.domain.user;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.coupon.UserCoupon;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.support.constant.*;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.time.LocalDateTime;
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


    @Column(nullable = false)
    @Comment("사용자 이름")
    private String name;

}
