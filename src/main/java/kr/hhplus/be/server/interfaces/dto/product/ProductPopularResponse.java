package kr.hhplus.be.server.interfaces.dto.product;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;
import java.util.List;

@Schema(description = "인기 상품 목록 아이템 DTO")

public record ProductPopularResponse(
        @Schema(description = "기준 날짜", example = "2025-01-01T00:00:00")
        LocalDateTime standardDate,

        @Schema(description = "인기 상품 목록")
        List<ProductPolularItemResponse> products
) {}
