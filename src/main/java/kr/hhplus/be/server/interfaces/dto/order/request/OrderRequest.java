package kr.hhplus.be.server.interfaces.dto.order.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Schema(description = "주문 요청 DTO")
public class OrderRequest {

    @Schema(description = "주문 생성 요청")
    public record CreateOrderRequest(
            @Schema(description = "사용자 ID", example = "1")
            Long userId,
            @Schema(description = "주문 상품 목록")
            List<OrderItemRequest> items
    ) {}

    @Schema(description = "주문 상품 정보")
    public record OrderItemRequest(
            @Schema(description = "상품 ID", example = "1")
            Long productId,
            @Schema(description = "주문 수량", example = "2")
            Integer quantity
    ) {}

    @Schema(description = "주문 목록 조회 요청")
    public record OrderListRequest(
            @Schema(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,

            @Schema(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {}

}
