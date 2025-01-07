package kr.hhplus.be.server.interfaces.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.support.constant.ProductStatus;

import java.time.LocalDateTime;

@Schema(description = "삼품 조회 응답 DTO")
public record ProductResponse(
        @Schema(description = "상품 ID", example = "1")
        Long productId,
        @Schema(description="상품 이름", example = "항해 기념품")
        String productName,
        @Schema(description = "상품 가격", example = "25000")
        Long price,
        @Schema(description = "상픔 제고", example = "100")
        Integer stock,
        @Schema(description = "상품 상태", example="판매중")
        ProductStatus status,
        @Schema(description = "생성 일자", example="2025-01-01T10:30:00")
        LocalDateTime createdAt,
        @Schema(description = "수정 일자", example="2025-01-02T10:30:00")
        LocalDateTime updatedAt
) {

}
