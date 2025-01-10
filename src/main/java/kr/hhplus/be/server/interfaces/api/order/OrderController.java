package kr.hhplus.be.server.interfaces.api.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import kr.hhplus.be.server.application.order.OrderCommand;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.order.OrderListResult;
import kr.hhplus.be.server.application.order.OrderResult;
import kr.hhplus.be.server.interfaces.dto.order.CreateOrderRequest;
import kr.hhplus.be.server.interfaces.dto.order.OrderItemResponse;
import kr.hhplus.be.server.interfaces.dto.order.OrderResponse;
import kr.hhplus.be.server.support.constant.OrderStatus;
import kr.hhplus.be.server.support.constant.SuccessCode;
import kr.hhplus.be.server.support.http.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Tag(name = "orders", description = "주문 API")
@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderFacade orderFacade;

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    @PostMapping
    public ResponseEntity<CustomApiResponse<OrderResponse>> createOrder(
            @RequestBody CreateOrderRequest request
    ) {
        OrderCommand command = new OrderCommand(
                request.userId(),
                request.items().stream()
                        .map(item -> new OrderCommand.OrderItem(
                                item.productId(),
                                item.quantity()
                        ))
                        .toList()
        );

        OrderResult result = orderFacade.createOrder(command);
        return ResponseEntity.ok(CustomApiResponse.of(
                SuccessCode.ORDER_CREATED,
                result.toResponse()
        ));
    }

    @Operation(summary = "주문 조회", description = "주문 정보를 조회합니다.")
    @GetMapping("/{orderId}")
    public ResponseEntity<CustomApiResponse<OrderResponse>> getOrder(
            @Parameter(description = "주문 ID", required = true)
            @PathVariable Long orderId
    ) {
        OrderResult result = orderFacade.getOrder(orderId);
        return ResponseEntity.ok(CustomApiResponse.of(
                SuccessCode.ORDER_FOUND,
                result.toResponse()
        ));
    }
}
