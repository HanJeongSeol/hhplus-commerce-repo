package kr.hhplus.be.server.interfaces.dto.point.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.application.point.response.PointResult;

import java.time.LocalDateTime;
@Schema(description = "포인트 응답 DTO")
public class PointResponse {
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
        ) {
                public static PointChargeResponse toResponse(PointResult.PointChargeResult result) {
                        return new PointChargeResponse(
                                result.userId(),
                                result.userName(),
                                result.balance(),
                                result.chargedAmount(),
                                LocalDateTime.now()
                        );
                }
        }

        @Schema(description = "포인트 조회 응답")
        public record PointBalanceResponse(
                Long userId,
                String userName,
                Long balance
        ) {
                public static PointBalanceResponse toResponse(PointResult.PointBalanceResult result){
                        return new PointBalanceResponse(
                                result.userId(),
                                result.userName(),
                                result.balance()
                        );
                }
        }
}