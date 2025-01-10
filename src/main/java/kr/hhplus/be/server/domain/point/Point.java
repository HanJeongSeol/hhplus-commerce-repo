package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.*;
import org.hibernate.annotations.Comment;


@Entity
@Table(name = "point")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Point extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "point_id")
    @Comment("포인트 식별자")
    private Long pointId;

    @Column
    @Comment("사용자 식별자")
    private Long userId;

    @Column(nullable = false)
    @Comment("포인트 잔고")
    private Long balance;


    // 최대 포인트 한도 (상수로 관리)
    private static final long MAX_POINT_AMOUNT = 10_000_000L;

    /**
     * 포인트 충전
     */
    public void charge(Long amount){
        validateChargeAmount(amount);
        this.balance += amount;
    }

    /**
     * 포인트 사용
     */
    public void use(Long amount){
        validateUseAmount(amount);
        this.balance -= amount;
    }

    private void validateChargeAmount(Long amount){
        if(amount <= 0 ){
            throw new BusinessException(ErrorCode.INVALID_POINT_AMOUNT);
        }
        if (this.balance + amount > MAX_POINT_AMOUNT){
            throw new BusinessException(ErrorCode.POINT_EXCEED_MAX_VALUE);
        }
    }

    private void validateUseAmount(Long amount){
        if(amount <=0){
            throw new BusinessException(ErrorCode.INVALID_POINT_AMOUNT);
        }
        if(this.balance < amount){
            throw new BusinessException(ErrorCode.INSUFFICIENT_POINT_BALANCE);
        }
    }



}
