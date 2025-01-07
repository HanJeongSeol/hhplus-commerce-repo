package kr.hhplus.be.server.interfaces.dto.point;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 충전 요청 DTO")
public record PointChargeRequest(
        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "충전 금액", example = "50000")
        Long amount
) {}
