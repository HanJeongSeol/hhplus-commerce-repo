package kr.hhplus.be.server.infra.product;

import com.querydsl.core.types.Projections;
import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.order.QOrder;
import kr.hhplus.be.server.domain.order.QOrderLine;
import kr.hhplus.be.server.domain.product.ProductPopularQueryDto;
import kr.hhplus.be.server.domain.product.QProduct;
import kr.hhplus.be.server.support.constant.OrderStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProductQueryDslRepositoryImpl implements ProductQueryDslRepository {
    private final JPAQueryFactory queryFactory;
    private final QProduct product = QProduct.product;
    private final QOrderLine orderLine = QOrderLine.orderLine;
    private final QOrder order = QOrder.order;

    @Override
    public List<ProductPopularQueryDto> findPopularProducts() {
        LocalDateTime endDate = LocalDateTime.now();
        LocalDateTime startDate = endDate.minusDays(3);

        return queryFactory
                .select(Projections.constructor(ProductPopularQueryDto.class,
                        product.productId,
                        product.name,
                        orderLine.count(),
                        product.price,
                        product.stock,
                        product.status))
                .from(product)
                .leftJoin(orderLine).on(orderLine.productId.eq(product.productId))
                .leftJoin(order).on(orderLine.orderId.eq(order.orderId))
                .where(
                        order.status.eq(OrderStatus.COMPLETED),
                        order.createdAt.between(startDate, endDate)
                )
                .groupBy(
                        product.productId,
                        product.name,
                        product.price,
                        product.stock,
                        product.status
                )
                .orderBy(orderLine.count().desc())
                .limit(5)
                .fetch();
    }

}
