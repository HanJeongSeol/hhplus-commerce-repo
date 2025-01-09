package kr.hhplus.be.server.infra.product;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.order.QOrder;
import kr.hhplus.be.server.domain.order.QOrderDetail;
import kr.hhplus.be.server.domain.payment.QPayment;
import kr.hhplus.be.server.domain.product.ProductPopularQueryDto;
import kr.hhplus.be.server.domain.product.QProduct;
import kr.hhplus.be.server.support.constant.PaymentStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductQueryDslRepositoryImpl implements ProductQueryDslRepository{

    private final JPAQueryFactory queryFactory;

    private final QProduct product = QProduct.product;
    private final QOrderDetail orderDetail = QOrderDetail.orderDetail;
    private final QOrder order = QOrder.order;
    private final QPayment payment = QPayment.payment;

    @Override
    public List<ProductPopularQueryDto> findPopularProducts() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(3);

        return queryFactory
                .select(Projections.constructor(ProductPopularQueryDto.class,
                        product.productId,
                        product.name,
                        orderDetail.count(),
                        product.price,
                        product.stock,
                        product.status))
                .from(product)
                .leftJoin(orderDetail).on(orderDetail.productId.eq(product.productId))
                .leftJoin(order).on(orderDetail.orderId.eq(order.orderId))
                .leftJoin(payment).on(payment.orderId.eq(order.orderId))
                .where(
                        payment.status.eq(PaymentStatus.PAID),
                        payment.createdAt.between(startDate, endDate)
                )
                .groupBy(
                        product.productId,
                        product.name,
                        product.price,
                        product.stock,
                        product.status
                )
                .orderBy(orderDetail.count().desc())
                .limit(5)
                .fetch();
    }
}
