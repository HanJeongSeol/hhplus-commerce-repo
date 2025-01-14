package kr.hhplus.be.server.config;

import kr.hhplus.be.server.domain.order.Order;
import kr.hhplus.be.server.domain.order.OrderLine;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.product.Product;
import kr.hhplus.be.server.support.constant.OrderStatus;
import kr.hhplus.be.server.support.constant.ProductStatus;

import java.util.ArrayList;
import java.util.List;

public abstract class TestUtil {

    public static Product createProduct() {
        return Product.builder()
                .productId(1L)
                .name("항해 기념품")
                .price(10000L)
                .stock(100)
                .status(ProductStatus.ON_SALE)
                .build();
    }

    public static Point createTestPoint() {
        return Point.builder()
                .pointId(1L)
                .userId(1L)
                .balance(1000L)
                .build();
    }

    public static Order createTestOrder() {
        Order order = Order.create(1L);
        order.addOrderLine(createTestOrderLine());
        return order;
    }

    public static OrderLine createTestOrderLine() {
        return OrderLine.createOrderLine(1L, 1L, 2, 10000L);
    }

    public static List<OrderLine> createTestOrderLines() {
        List<OrderLine> orderLines = new ArrayList<>();
        orderLines.add(OrderLine.createOrderLine(1L, 1L, 2, 10000L));
        orderLines.add(OrderLine.createOrderLine(1L, 2L, 1, 20000L));
        return orderLines;
    }
} 