package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.domain.order.dto.OrderInfo;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.constant.OrderStatus;
import kr.hhplus.be.server.support.exception.BusinessException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("주문 서비스 테스트")
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    private Long testUserId;
    private Order testOrder;
    private List<OrderInfo.OrderLineProduct> testOrderProducts;
    private OrderInfo.OrderDetail testOrderDetail;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
        testOrderProducts = List.of(
                new OrderInfo.OrderLineProduct(1L, 2, 10000L),
                new OrderInfo.OrderLineProduct(2L, 1, 20000L)
        );

        testOrder = Order.create(testUserId);
        testOrder.addOrderLine(OrderLine.createOrderLine(1L, 1L, 2, 10000L));
        testOrder.addOrderLine(OrderLine.createOrderLine(1L, 2L, 1, 20000L));
    }
    @BeforeEach
    void setUpMocks() {
        reset(orderRepository); // Mock 객체 초기화
    }

    @Nested
    @DisplayName("주문 생성")
    class CreateOrder {
        @Test
        @DisplayName("주문 생성 성공")
        void userId와_상품목록이_주어지면_주문_생성() {
            // given
            Order savedOrder =  Order.builder()
                    .orderId(1L)
                    .userId(1L)
                    .status(OrderStatus.PENDING)
                    .totalPrice(0L)
                    .build();

            List<OrderLine> orderLines = testOrderProducts.stream()
                    .map(product -> OrderLine.createOrderLine(
                            savedOrder.getOrderId(),
                            product.productId(),
                            product.quantity(),
                            product.price()
                    )).toList();

            given(orderRepository.save(any(Order.class)))
                    .willReturn(savedOrder);
            given(orderRepository.saveAll(anyList()))
                    .willReturn(orderLines);

            // when
            OrderInfo.OrderDetail result = orderService.createOrder(testUserId, testOrderProducts);

            // then
            assertThat(result).isNotNull();
            assertThat(result.userId()).isEqualTo(testUserId);
            assertThat(result.status()).isEqualTo(OrderStatus.PENDING);
            assertThat(result.totalPrice()).isEqualTo(40000L); // 20000L + 20000L
            assertThat(result.orderLines()).hasSize(2);

            verify(orderRepository).save(any(Order.class));
            verify(orderRepository).saveAll(anyList());
        }

        @Test
        @DisplayName("주문 상품 목록이 비어있으면 예외 발생")
        void 주문상품목록이_비어있으면_INVALID_ORDER_REQUEST_예외_발생() {
            // given
            Long userId = 1L;
            List<OrderInfo.OrderLineProduct> emptyProducts = List.of();

            // when & then
            assertThatThrownBy(() -> orderService.createOrder(userId, emptyProducts))
                    .isInstanceOf(BusinessException.class)
                    .hasFieldOrPropertyWithValue("errorCode", ErrorCode.INVALID_ORDER_REQUEST);
        }

        @Test
        @DisplayName("주문 수량이 0이나 음수인 경우 예외 발생")
        void 주문_수량이_0_이하면_INVALID_ORDER_QUANTITY_예외_발생() {
            // given
            OrderInfo.OrderLineProduct invalidProduct = OrderInfo.OrderLineProduct.from(10L, 0, 10000L);

            // when & then
            assertThatThrownBy(() -> orderService.createOrder(testUserId, List.of(invalidProduct)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.INVALID_ORDER_QUANTITY.getMessage());
        }

        @Test
        @DisplayName("주문 가격이 0이나 음수인 경우 예외 발생")
        void 주문_가격이_0_이하면_INVALID_PRODUCT_PRICE_예외_발생() {
            // given
            OrderInfo.OrderLineProduct invalidProduct = OrderInfo.OrderLineProduct.from(1L, 1, 0L);

            // when & then
            assertThatThrownBy(() -> orderService.createOrder(testUserId, List.of(invalidProduct)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.INVALID_PRODUCT_PRICE.getMessage());
        }
    }
}