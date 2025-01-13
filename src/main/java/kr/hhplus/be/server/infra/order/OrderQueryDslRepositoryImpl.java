package kr.hhplus.be.server.infra.order;

import com.querydsl.jpa.impl.JPAQueryFactory;
import kr.hhplus.be.server.application.order.OrderResult;
import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderLine;
import kr.hhplus.be.server.domain.order.QOrder;
import kr.hhplus.be.server.domain.order.QOrderLine;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class OrderQueryDslRepositoryImpl implements OrderQueryDslRepository {
    private final JPAQueryFactory queryFactory;
    private final QOrder order = QOrder.order;
    private final QOrderLine orderLine = QOrderLine.orderLine;

    @Override
    public Optional<OrderResult> findByIdWithLines(Long orderId) {
        Order foundOrder = queryFactory
                .selectFrom(order)
                .where(order.orderId.eq(orderId))
                .fetchOne();

        if (foundOrder == null) {
            return Optional.empty();
        }

        List<OrderLine> orderLines = queryFactory
                .selectFrom(orderLine)
                .where(orderLine.orderId.eq(orderId))
                .fetch();

        return Optional.of(OrderResult.of(foundOrder, orderLines));
    }

    @Override
    public List<OrderResult> findByUserIdWithLines(Long userId) {
        List<Order> orders = queryFactory
                .selectFrom(order)
                .where(order.userId.eq(userId))
                .fetch();

        if (orders.isEmpty()) {
            return List.of();
        }

        List<Long> orderIds = orders.stream()
                .map(Order::getOrderId)
                .toList();

        List<OrderLine> allOrderLines = queryFactory
                .selectFrom(orderLine)
                .where(orderLine.orderId.in(orderIds))
                .fetch();

        Map<Long, List<OrderLine>> orderLineMap = allOrderLines.stream()
                .collect(Collectors.groupingBy(OrderLine::getOrderId));

        return orders.stream()
                .map(order -> OrderResult.of(
                        order,
                        orderLineMap.getOrDefault(order.getOrderId(), List.of())
                ))
                .toList();
    }

    @Override
    public Page<OrderResult> findByUserIdWithLines(Long userId, Pageable pageable) {
        List<Order> orders = queryFactory
                .selectFrom(order)
                .where(order.userId.eq(userId))
                .orderBy(order.orderId.desc())
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        if (!orders.isEmpty()) {
            List<Long> orderIds = orders.stream()
                    .map(Order::getOrderId)
                    .toList();

            List<OrderLine> allOrderLines = queryFactory
                    .selectFrom(orderLine)
                    .where(orderLine.orderId.in(orderIds))
                    .fetch();

            Map<Long, List<OrderLine>> orderLineMap = allOrderLines.stream()
                    .collect(Collectors.groupingBy(OrderLine::getOrderId));

            List<OrderResult> orderResults = orders.stream()
                    .map(order -> OrderResult.of(
                            order,
                            orderLineMap.getOrDefault(order.getOrderId(), List.of())
                    ))
                    .toList();

            Long total = queryFactory
                    .select(order.count())
                    .from(order)
                    .where(order.userId.eq(userId))
                    .fetchOne();

            return new PageImpl<>(orderResults, pageable, total);
        }

        return Page.empty(pageable);
    }
}