package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.orderDetail.OrderDetail;
import kr.hhplus.be.server.domain.popularProduct.PopularProducts;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "product")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long productId;

    @Column(nullable = false)
    private String productName;

    @OneToMany(mappedBy = "product")  // orderDetail 엔티티의 product 필드 참조
    private List<OrderDetail> orderDetails = new ArrayList<>();

    @OneToOne(mappedBy = "product")  // popularProducts 엔티티의 product 필드 참조
    private PopularProducts popularProducts;

    @Column(nullable = false)
    private Long amount;

    @Column(nullable = false)
    private Long stock;

    @Enumerated(EnumType.STRING)
    private ProductStatus status;

    @CreatedDate
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 비즈니스 메서드
    public boolean isAvailable() {
        return status == ProductStatus.IN_STOCK && !isSoldOut();
    }

    public void decreaseStock(Long quantity) {
        validateStockDecrease(quantity);
        this.stock -= quantity;
        updateStatus();
    }

    public boolean hasStock(Long quantity) {
        return this.stock >= quantity;
    }

    public boolean isSoldOut() {
        return this.stock <= 0;
    }

    public void updateStatus() {
        if (isSoldOut()) {
            this.status = ProductStatus.OUT_OF_STOCK;
        }
    }

    private void validateStockDecrease(Long quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("수량은 0보다 커야 합니다.");
        }
        if (!hasStock(quantity)) {
            throw new IllegalArgumentException("재고가 부족합니다.");
        }
    }
}