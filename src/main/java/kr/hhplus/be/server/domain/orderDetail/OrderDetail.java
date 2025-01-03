package kr.hhplus.be.server.domain.orderDetail;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.product.Product;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

@Entity
@Table(name = "order_detail")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderDetail {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long orderDetailId;

    private Long orderId;
    private Long productId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "orderId",
            referencedColumnName = "orderId",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Order order;

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
    private Long stock;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Long totalAmount;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 비즈니스 메서드
    public void calculateAmount() {
        validateCalculation();
        this.totalAmount = this.amount * this.stock;
    }

    public boolean validateStock() {
        if (product == null) {
            throw new IllegalStateException("상품 정보가 없습니다.");
        }
        return product.hasStock(this.stock);
    }

    private void validateCalculation() {
        if (this.amount <= 0) {
            throw new IllegalArgumentException("상품 금액은 0보다 커야 합니다.");
        }
        if (this.stock <= 0) {
            throw new IllegalArgumentException("주문 수량은 0보다 커야 합니다.");
        }
    }
}