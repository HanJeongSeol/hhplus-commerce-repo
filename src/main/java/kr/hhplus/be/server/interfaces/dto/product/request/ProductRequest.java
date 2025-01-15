package kr.hhplus.be.server.interfaces.dto.product.request;

import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@Schema(description = "Product 요청 DTO")
public class ProductRequest {
    public record ProductInfoRequest(
            @Schema(description = "페이지 번호", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Schema(description = "페이지 크기", example = "10")
            @RequestParam(defaultValue = "10") int size
    ) {}

    public record ProductDetailInfoRequest(
            @Schema(description = "상품 아이디", example = "1")
            @PathVariable Long productId
    ) {

    }
}
