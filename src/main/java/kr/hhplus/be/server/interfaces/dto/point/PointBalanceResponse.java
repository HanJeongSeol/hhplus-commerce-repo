package kr.hhplus.be.server.interfaces.dto.point;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "포인트 조회 응답 DTO")
public record PointBalanceResponse(
        @Schema(description = "사용자 ID", example = "1")
        Long userId,

        @Schema(description = "사용자 이름", example = "홍길동")
        String userName,

        @Schema(description = "잔액", example = "150000")
        Long balance
){}
