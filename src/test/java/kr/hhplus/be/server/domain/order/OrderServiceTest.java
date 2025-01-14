package kr.hhplus.be.server.domain.order;

import kr.hhplus.be.server.application.order.OrderLineResult;
import kr.hhplus.be.server.application.order.OrderResult;
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
    private List<OrderLineProduct> testOrderProducts;
    private OrderResult testOrderResult;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
        testOrderProducts = List.of(
                new OrderLineProduct(1L, 2, 10000L),
                new OrderLineProduct(2L, 1, 20000L)
        );

        testOrder = Order.createOrder(testUserId);
        testOrder.addOrderLine(OrderLine.createOrderLine(1L, 2, 10000L));
        testOrder.addOrderLine(OrderLine.createOrderLine(2L, 1, 20000L));

        testOrderResult = new OrderResult(
                1L,
                testUserId,
                OrderStatus.PENDING,
                40000L,
                LocalDateTime.now(),
                List.of(
                        new OrderLineResult(1L, testOrder.getOrderId(), 1L, 2, 20000L),
                        new OrderLineResult(2L, testOrder.getOrderId(), 2L, 1, 20000L)
                )
        );
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
        void createOrderSuccess() {
            // given
            given(orderRepository.save(any(Order.class)))
                    .willReturn(testOrder);

            // when
            Order result = orderService.createOrder(testUserId, testOrderProducts);

            // then
            assertThat(result.getUserId()).isEqualTo(testUserId);
            assertThat(result.getTotalPrice()).isEqualTo(40000L);
            assertThat(result.getStatus()).isEqualTo(OrderStatus.PENDING);
            verify(orderRepository, times(1)).save(any(Order.class));
        }

        @Test
        @DisplayName("주문 상품 목록이 비어있으면 예외 발생")
        void createOrderWithEmptyProducts() {
            assertThatThrownBy(() -> orderService.createOrder(testUserId, List.of()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.INVALID_ORDER_REQUEST.getMessage());
        }

        @Test
        @DisplayName("주문 수량이 0이나 음수인 경우 예외 발생")
        void 주문_수량이_0_이하면_INVALID_ORDER_QUANTITY_예외_전달() {
            // given
            OrderLineProduct invalidProduct = OrderLineProduct.of(10L, 0, 10000L);

            // when & then
            assertThatThrownBy(() -> orderService.createOrder(testUserId, List.of(invalidProduct)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.INVALID_ORDER_QUANTITY.getMessage());
        }

        @Test
        @DisplayName("주문 가격이 0이나 음수인 경우 예외 발생")
        void 주문_가격이_0_이하면_INVALID_PRODUCT_PRICE_예외_발생() {
            // given
            OrderLineProduct invalidProduct = OrderLineProduct.of(1L, 1, 0L);

            // when & then
            assertThatThrownBy(() -> orderService.createOrder(testUserId, List.of(invalidProduct)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.INVALID_PRODUCT_PRICE.getMessage());
        }
    }

    @Nested
    @DisplayName("주문 조회")
    class GetOrder {
        @Test
        @DisplayName("주문 조회 성공")
        void getOrderSuccess() {
            // given
            given(orderRepository.findByIdWithLines(1L))
                    .willReturn(Optional.of(testOrderResult));

            // when
            OrderResult result = orderService.getOrder(1L);

            // then
            assertThat(result).isNotNull();
            assertThat(result.orderId()).isEqualTo(1L);
            assertThat(result.orderLines()).hasSize(2);
            assertThat(result.totalPrice()).isEqualTo(40000L);
        }

        @Test
        @DisplayName("존재하지 않는 주문 조회시 예외 발생")
        void getOrderNotFound() {
            given(orderRepository.findByIdWithLines(999L))
                    .willReturn(Optional.empty());

            assertThatThrownBy(() -> orderService.getOrder(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.ORDER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("사용자 주문 목록 조회")
    class GetUserOrders {
        @Test
        @DisplayName("사용자 주문 목록 조회 성공")
        void getUserOrdersSuccess() {
            // given
            List<OrderResult> orderResults = List.of(testOrderResult);
            given(orderRepository.findByUserIdWithLines(testUserId))
                    .willReturn(orderResults);

            // when
            List<OrderResult> results = orderService.getUserOrders(testUserId);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).userId()).isEqualTo(testUserId);
            verify(orderRepository).findByUserIdWithLines(testUserId);
        }
    }

}