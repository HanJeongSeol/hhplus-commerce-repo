package kr.hhplus.be.server.interfaces.dto.product.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.product.response.ProductResult;
import kr.hhplus.be.server.support.constant.ProductStatus;

import java.time.LocalDateTime;

@Schema(description = "Product 응답 DTO")
public class ProductResponse {
    public record ProductInfoResponse(
            @Schema(description = "상품 ID", example = "1")
            Long productId,
            @Schema(description = "상품 이름", example = "항해 기념품")
            String name,
            @Schema(description = "상품 가격", example = "25000")
            Long price,
            @Schema(description = "재고 수량", example = "100")
            Integer stock,
            @Schema(description = "상품 상태", example = "ON_SALE")
            ProductStatus status,
            @Schema(description = "생성일시")
            LocalDateTime createdAt,
            @Schema(description = "수정일시")
            LocalDateTime updatedAt
    ) {
        public static ProductInfoResponse toResponse(ProductResult.ProductInfoResult result) {
            return new ProductInfoResponse(
                    result.productId(),
                    result.productName(),
                    result.price(),
                    result.stock(),
                    result.status(),
                    result.createdAt(),
                    result.updatedAt()
            );
        }
    }

    public record ProductPopularResponse(
            Integer rank,
            Long productId,
            String productName,
            Long salesCount,
            Long price,
            Integer stock,
            ProductStatus status
    ) {
        public static ProductPopularResponse toResponse(ProductResult.ProductPopularResult result) {
            return new ProductPopularResponse(
                    result.rank(),
                    result.productId(),
                    result.productName(),
                    result.salesCount(),
                    result.price(),
                    result.stock(),
                    result.status()
            );
        }


    }

}
