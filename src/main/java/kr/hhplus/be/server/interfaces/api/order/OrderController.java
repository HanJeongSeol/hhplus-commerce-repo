package kr.hhplus.be.server.interfaces.api.order;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import kr.hhplus.be.server.application.order.OrderFacade;
import kr.hhplus.be.server.application.order.request.OrderCommand;
import kr.hhplus.be.server.interfaces.dto.order.request.OrderRequest;
import kr.hhplus.be.server.interfaces.dto.order.response.OrderResponse;
import kr.hhplus.be.server.support.constant.OrderStatus;
import kr.hhplus.be.server.support.constant.SuccessCode;
import kr.hhplus.be.server.support.http.CustomApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Tag(name = "orders", description = "주문 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderFacade orderFacade;

    @Operation(summary = "주문 생성", description = "새로운 주문을 생성합니다.")
    @PostMapping
    public ResponseEntity<CustomApiResponse<OrderResponse.OrderInfoResponse>> createOrder(
            @RequestBody OrderRequest.CreateOrderRequest request) {

        var command = OrderCommand.CreateOrder.from(request);
        var result = orderFacade.createOrder(command);
        var response = OrderResponse.OrderInfoResponse.from(result);
        return ResponseEntity.ok(CustomApiResponse.of(SuccessCode.ORDER_CREATED, response));
    }
}
