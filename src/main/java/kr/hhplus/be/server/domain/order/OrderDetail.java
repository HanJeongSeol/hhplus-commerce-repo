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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "order_id",
            referencedColumnName = "order_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Order order;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_id",
            referencedColumnName = "product_id",
            insertable = false,
            updatable = false,
            foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT)
    )
    private Product product;

    @Column(nullable = false)
    @Comment("주문 수량")
    private Integer stock;

    @Column(nullable = false)
    @Comment("총 금액")
    private Long totalAmount;



    public static OrderDetail createOrderDetail(Long productId, Integer stock, Long price) {
        OrderDetail orderDetail = new OrderDetail();
        orderDetail.productId = productId;
        orderDetail.stock = stock;
        orderDetail.totalAmount = stock * price;
        return orderDetail;
    }

    public void assignOrder(Order order) {
        this.order = order;
        this.orderId = order.getOrderId();
    }

    /**
     * 주문 총 가격 설정
     * - 상품 가격 * 주문 수량
     */
    public void setTotalAmount(){
        this.totalAmount = product.getPrice() * this.stock;
    }

}
