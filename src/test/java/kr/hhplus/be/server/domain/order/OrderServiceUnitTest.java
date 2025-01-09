package kr.hhplus.be.server.domain.order;

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

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
@DisplayName("주문 서비스 테스트")
public class OrderServiceUnitTest {
    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    private Long testUserId;
    private Order testOrder;
    private List<OrderDetailProduct> testOrderProductList;
    private List<OrderDetail> testOrderDetailList;

    @BeforeEach
    void setUp() {
        testUserId = 1L;

        // 테스트용 주문 상품 정보
        testOrderProductList = List.of(
                OrderDetailProduct.of(1L, 2, 10000L),
                OrderDetailProduct.of(2L, 1, 20000L)
        );

        // 테스트용 주문 상세
        testOrderDetailList = new ArrayList<>();
        for (OrderDetailProduct p : testOrderProductList) {
            testOrderDetailList.add(
                    OrderDetail.createOrderDetail(p.productId(), p.quantity(), p.price())
            );
        }

        // 테스트용 주문 생성
        testOrder = Order.createOrder(testUserId);
        // OrderDetail 리스트 전체를 주문에 추가
        testOrderDetailList.forEach(testOrder::addOrderDetail);
    }

    @Nested
    @DisplayName("주문 생성")
    class CreateOrder {

        @Test
        @DisplayName("주문 생성 성공")
        void userId와_OrderProduct_목록이_주어지면_Order와_OrderDetail이_생성된다() {
            // given
            given(orderRepository.save(any(Order.class))).willReturn(testOrder);

            // when
            Order result = orderService.createOrder(testUserId, testOrderProductList);

            for (int i = 0; i < result.getOrderDetails().size(); i++) {
                OrderDetail detail = result.getOrderDetails().get(i);
                OrderDetailProduct product = testOrderProductList.get(i);

                assertThat(detail.getProductId()).isEqualTo(product.productId());
                assertThat(detail.getStock()).isEqualTo(product.quantity());
                assertThat(detail.getTotalAmount()).isEqualTo(product.price() * product.quantity());
            }
            verify(orderRepository).save(any(Order.class));

        }

        @Test
        @DisplayName("주문 상품 목록이 비어있으면 예외 발생")
        void 주문상품_목록이_비어있으면_INVALID_ORDER_REQUEST_예외를_전달(){
            // when & then
            assertThatThrownBy(() -> orderService.createOrder(testUserId, List.of()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.INVALID_ORDER_REQUEST.getMessage());
        }

        @Test
        @DisplayName("주문 수량이 0이나 음수인 경우 예외 발생")
        void 주문_수량이_0_이하면_INVALID_ORDER_QUANTITY_예외_전달() {
            // given
            OrderDetailProduct invalidProduct = OrderDetailProduct.of(10L, 0, 10000L);

            // when & then
            assertThatThrownBy(() -> orderService.createOrder(testUserId, List.of(invalidProduct)))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.INVALID_ORDER_QUANTITY.getMessage());
        }

        @Test
        @DisplayName("주문 가격이 0이나 음수인 경우")
        void 주문_가격이_0_이하면_INVALID_PRODUCT_PRICE_예외_발생() {
            // given
            OrderDetailProduct invalidProduct = OrderDetailProduct.of(1L, 1, 0L);

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
        @DisplayName("주문 상세 조회 성공")
        void userId와_orderId가_주어지면_주문과_주문상세내역을_조회한다() {
            // given
            given(orderRepository.findByIdWithDetails(testOrder.getOrderId()))
                    .willReturn(Optional.of(testOrder));

            // when
            Order result = orderService.getOrder(testOrder.getOrderId());

            // then
            assertThat(result.getOrderId()).isEqualTo(testOrder.getOrderId());
            // 여러 OrderDetail이 있을 수 있으므로, 사이즈 검증
            assertThat(result.getOrderDetails()).hasSize(testOrderDetailList.size());

            // 각 디테일이 잘 매핑되었는지 검증
            for (int i = 0; i < result.getOrderDetails().size(); i++) {
                OrderDetail actualDetail = result.getOrderDetails().get(i);
                OrderDetail expectedDetail = testOrderDetailList.get(i);

                assertThat(actualDetail.getProductId()).isEqualTo(expectedDetail.getProductId());
                assertThat(actualDetail.getStock()).isEqualTo(expectedDetail.getStock());
                assertThat(actualDetail.getTotalAmount()).isEqualTo(expectedDetail.getTotalAmount());
            }
        }
        @Test
        @DisplayName("존재하지 않는 주문 조회 요청 시 예외")
        void 존재하지_않는_주문아이디라면_ORDER_NOT_FOUND_예외_전달() {
            // given
            given(orderRepository.findByIdWithDetails(999L))
                    .willReturn(Optional.empty());

            // when & then
            assertThatThrownBy(() -> orderService.getOrder(999L))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.ORDER_NOT_FOUND.getMessage());
        }
    }

    @Nested
    @DisplayName("주문 상태 변경")
    class ChangeOrderStatus {

        @Test
        @DisplayName("주문 완료 처리 성공")
        void 주문_아이디가_주어지면_주문을_완료처리한다() {
            // given
            given(orderRepository.findByIdWithDetails(testOrder.getOrderId()))
                    .willReturn(Optional.of(testOrder));
            given(orderRepository.save(any(Order.class))).willReturn(testOrder);

            // when
            Order result = orderService.completeOrder(testOrder.getOrderId());

            // then
            assertThat(result.getStatus()).isEqualTo(OrderStatus.COMPLETED);
        }

        @Test
        @DisplayName("주문 상태 성공 변경 처리 실패")
        void 주문_아이디가_주어지고_주문_상태가_PENDING이_아니라면_INVALID_ORDER_STATUS_예외_발생() {
            // given
            Order invalidOrder = Order.builder()
                    .orderId(2L)
                    .status(OrderStatus.COMPLETED)
                    .build();
            given(orderRepository.findByIdWithDetails(invalidOrder.getOrderId()))
                    .willReturn(Optional.of(invalidOrder));

            // when & then
            assertThatThrownBy(() -> orderService.cancelOrder(invalidOrder.getOrderId()))
                    .isInstanceOf(BusinessException.class)
                    .hasMessage(ErrorCode.INVALID_ORDER_STATUS.getMessage());
        }

        @Test
        @DisplayName("주문 취소처리 성공")
        void 주문_아이디가_주어지면_주문_취소처리한다() {
            // given
            given(orderRepository.findByIdWithDetails(testOrder.getOrderId()))
                    .willReturn(Optional.of(testOrder));
            given(orderRepository.save(any(Order.class))).willReturn(testOrder);

            // when
            Order result = orderService.cancelOrder(testOrder.getOrderId());

            // then
            assertThat(result.getStatus()).isEqualTo(OrderStatus.CANCELLED);
        }
    }

    @Nested
    @DisplayName("사용자 주문 목록 조회")
    class GetUserOrders {

        @Test
        @DisplayName("사용자 주문 목록 조회 성공")
        void 사용자_아이디가_주어지면_주문목록을_조회한다() {
            // given
            given(orderRepository.findByUserIdWithDetails(testUserId))
                    .willReturn(List.of(testOrder));

            // when
            List<Order> results = orderService.getUserOrders(testUserId);

            // then
            assertThat(results).hasSize(1);
            assertThat(results.get(0).getUserId()).isEqualTo(testUserId);
        }
    }
}
