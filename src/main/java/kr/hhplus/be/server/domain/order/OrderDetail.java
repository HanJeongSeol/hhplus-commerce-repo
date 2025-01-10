package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import kr.hhplus.be.server.domain.product.Product;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "order_detail")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderDetail extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_detail_id")
    @Comment("주문 상세 내역 식별자")
    private Long orderDetailId;

    @Column(name = "order_id", nullable = false)
    @Comment("주문 식별자")
    private Long orderId;

    @Column(name = "product_id", nullable = false)
    @Comment("상품 식별자")
    private Long productId;

    @Column(nullable = false)
    @Comment("주문 수량")
    private Integer quantity;

    @Column(nullable = false)
    @Comment("총 금액")
    private Long totalPrice;



    public static OrderDetail createOrderDetail(Long productId, Integer quantity, Long price) {
        OrderDetail detail = new OrderDetail();
        detail.productId = productId;
        detail.quantity = quantity;
        detail.totalPrice = (long) quantity * price;
        return detail;
    }

    public void assignOrder(Long orderId){
        this.orderId = orderId;
    }
}
