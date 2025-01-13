package kr.hhplus.be.server.domain.order;

import jakarta.persistence.*;
import kr.hhplus.be.server.domain.BaseEntity;
import lombok.*;
import org.hibernate.annotations.Comment;

@Entity
@Table(name = "order_line")
@Getter
@Builder
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OrderLine extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name="order_line_id")
    @Comment("주문 상세 내역 식별자")
    private Long orderLineId;

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



    public static OrderLine createOrderLine(Long productId, Integer quantity, Long price) {
        OrderLine line = new OrderLine();
        line.productId = productId;
        line.quantity = quantity;
        line.totalPrice = (long) quantity * price;
        return line;
    }

    public void assignOrder(Long orderId){
        this.orderId = orderId;
    }
}
