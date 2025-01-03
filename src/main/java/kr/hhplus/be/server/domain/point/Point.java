package kr.hhplus.be.server.domain.point;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.user.User;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "point")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Point {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long pointId;

    private Long userId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "userId",
            referencedColumnName = "userId",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private User user;

    @Column(nullable = false)
    private Long balance;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 비즈니스 메서드
    public void charge(Long amount) {
        validateChargeAmount(amount);
        this.balance += amount;
    }

    public void use(Long amount) {
        validateUseAmount(amount);
        this.balance -= amount;
    }

    public boolean isAvailable(Long amount) {
        return this.balance >= amount;
    }

    public Long getBalance() {
        return this.balance;
    }

    private void validateChargeAmount(Long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("충전 금액은 0보다 커야 합니다.");
        }
    }

    private void validateUseAmount(Long amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("사용 금액은 0보다 커야 합니다.");
        }
        if (!isAvailable(amount)) {
            throw new IllegalArgumentException("잔액이 부족합니다.");
        }
    }
}