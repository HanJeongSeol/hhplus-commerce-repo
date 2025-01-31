package kr.hhplus.be.server.domain.product;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.constant.ProductStatus;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.*;
import org.hibernate.annotations.Comment;


@Entity
@Table(name = "product")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="product_id")
    @Comment("상품 식별자")
    private Long productId;

    @Column(nullable = false)
    @Comment("상품 이름")
    private String name;

    @Column(nullable = false)
    @Comment("상품 가격")
    private Long price;

    @Comment("상품 재고")
    private int stock;

    @Enumerated(EnumType.STRING)
    @Comment("상품 상태")
    private ProductStatus status;

    /**
     * 상품 재고 감소
     */
    public void decreaseStock(int quantity){
        validateStockDecrease(quantity);
        this.stock -= quantity;
        updateStatus();
    }

    private void validateStockDecrease(int quantity){
        if(quantity <= 0){
            throw new BusinessException(ErrorCode.INVALID_ORDER_QUANTITY);
        }
        if(this.stock<quantity){
            throw new BusinessException(ErrorCode.PRODUCT_OUT_OF_STOCK,this.productId);
        }
    }

    private void updateStatus(){
        this.status = (this.stock > 0)
                ? ProductStatus.ON_SALE : ProductStatus.SOLD_OUT;
    }


}