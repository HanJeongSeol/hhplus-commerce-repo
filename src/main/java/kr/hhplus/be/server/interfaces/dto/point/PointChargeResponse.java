package kr.hhplus.be.server.interfaces.dto.point;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.LocalDateTime;

@Schema(description = "포인트 충전 응답 DTO")
public record PointChargeResponse(
        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "사용자 이름", example = "설한정")
        String userName,

        @Schema(description = "잔액", example = "150000")
        Long balance,

        @Schema(description = "충전 금액", example = "50000")
        Long chargedAmount,

        @Schema(description = "충전 완료 시간", example = "2025-01-01T10:30:00")
        LocalDateTime createdDate
) {}