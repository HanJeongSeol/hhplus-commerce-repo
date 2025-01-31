package kr.hhplus.be.server.infra.order;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.QOrder;
import kr.hhplus.be.server.domain.order.QOrderDetail;
import kr.hhplus.be.server.domain.product.QProduct;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OrderQueryDslRepositoryImpl implements OrderQueryDslRepository {

    private final JPAQueryFactory queryFactory;

    private final QOrder order = QOrder.order;
    private final QOrderDetail orderDetail = QOrderDetail.orderDetail;
    private final QProduct product = QProduct.product;

    @Override
    public Optional<Order> findByIdWithDetails(Long orderId) {
        Order result = queryFactory
                .selectFrom(order)
                .leftJoin(order.orderDetails, orderDetail).fetchJoin()
                .leftJoin(orderDetail.product, product).fetchJoin()
                .where(order.orderId.eq(orderId))
                .fetchOne();

        return Optional.ofNullable(result);
    }

    @Override
    public List<Order> findByUserIdWithDetails(Long userId) {
        return queryFactory
                .selectFrom(order)
                .leftJoin(order.orderDetails, orderDetail).fetchJoin()
                .leftJoin(orderDetail.product, product).fetchJoin()
                .where(order.userId.eq(userId))
                .fetch();
    }

    @Override
    public Page<Order> findByUserIdWithDetails(Long userId, Pageable pageable) {
        List<Order> content= queryFactory
                .selectFrom(order)
                .leftJoin(order.orderDetails, orderDetail).fetchJoin()
                .leftJoin(orderDetail.product, product).fetchJoin()
                .where(order.userId.eq(userId))
                .orderBy(order.orderId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();


        Long total = queryFactory
                .select(order.count())
                .from(order)
                .where(order.userId.eq(userId))
                .fetchOne();

        return new PageImpl<>(content, pageable, total);
    }
}
