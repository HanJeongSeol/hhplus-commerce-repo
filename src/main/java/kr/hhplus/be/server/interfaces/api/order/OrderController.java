package kr.hhplus.be.server.interfaces.api.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.interfaces.dto.order.CreateOrderRequest;
import kr.hhplus.be.server.interfaces.dto.order.OrderItemResponse;
import kr.hhplus.be.server.interfaces.dto.order.OrderResponse;
import kr.hhplus.be.server.support.constant.OrderStatus;
import kr.hhplus.be.server.support.constant.SuccessCode;
import kr.hhplus.be.server.support.http.CustomApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Tag(name = "orders", description = "주문 API")
@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    @PostMapping
    public ResponseEntity<CustomApiResponse<OrderResponse>> createOrder(
            @RequestBody CreateOrderRequest request) {

        // Mock 응답 데이터 생성 (실제 구현 시에는 Facade 호출)
        OrderItemResponse orderItem = new OrderItemResponse(
                1L,
                "항해 기념품",
                request.items().get(0).quantity(),
                25000L,
                50000L
        );

        OrderItemResponse orderItem1 = new OrderItemResponse(
                2L,
                "항해 후드티",
                request.items().get(0).quantity(),
                15000L,
                30000L
        );

        List<OrderItemResponse> orderItemList = Stream.of(orderItem, orderItem1)
                .collect(Collectors.toList());
        System.out.println(orderItemList.get(0));
        System.out.println(orderItemList.get(1));
        OrderResponse response = new OrderResponse(
                1L,
                request.userId(),
                OrderStatus.PENDING,  // 초기 상태는 PENDING
                LocalDateTime.now(),
                50000L,
                orderItemList
        );

        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.ORDER_CREATED, response));
    }

    @Operation(summary = "주문 조회", description = "주문 정보를 조회합니다.")
    @GetMapping("/{orderId}")
    public ResponseEntity<CustomApiResponse<OrderResponse>> getOrder(
            @Parameter(description = "주문 ID", required = true)
            @PathVariable Long orderId) {

        // Mock 응답 데이터 생성 (실제 구현 시에는 Facade 호출)
        OrderItemResponse orderItem = new OrderItemResponse(
                1L,
                "항해 기념품",
                2,
                25000L,
                50000L
        );

        OrderResponse response = new OrderResponse(
                orderId,
                1L,
                OrderStatus.PENDING,
                LocalDateTime.now(),
                50000L,
                List.of(orderItem)
        );

        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.ORDER_FOUND, response));
    }
}
