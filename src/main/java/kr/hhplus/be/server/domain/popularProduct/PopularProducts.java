package kr.hhplus.be.server.domain.popularProduct;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.product.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "popular_product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PopularProducts {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long popularProductId;

    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "productId",
            referencedColumnName = "productId",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Product product;

    @Column(nullable = false)
    private Long salesCount;

    // 카운팅 업데이트 시간
    @Column(nullable = false)
    private LocalDateTime batchDate;

    @CreatedDate
    private LocalDateTime createdAt;

    // 비즈니스 메서드
    public void updateSalesCount(Long count) {
        validateSalesCount(count);
        this.salesCount = count;
        this.batchDate = LocalDateTime.now();
    }

    public boolean isValid() {
        return isWithinValidPeriod() && product != null;
    }

    private boolean isWithinValidPeriod() {
        LocalDateTime threeDaysAgo = LocalDateTime.now().minusDays(3);
        return this.batchDate.isAfter(threeDaysAgo);
    }

    private void validateSalesCount(Long count) {
        if (count < 0) {
            throw new IllegalArgumentException("판매량은 0보다 작을 수 없습니다.");
        }
    }
}