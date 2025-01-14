package kr.hhplus.be.server.application.point.response;

import io.swagger.v3.oas.annotations.media.Schema;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.user.User;

public class PointResult {
    @Schema(description = "포인트 충전")
    public record PointChargeResult(
            Long userId,
            String userName,
            Long balance,
            Long chargedAmount
    ) {
        public static PointChargeResult of(User user, Point point, Long chargedAmount) {
            return new PointChargeResult(
                    user.getUserId(),
                    user.getName(),
                    point.getBalance(),
                    chargedAmount
            );
        }
    }

    @Schema(description = "포인트 조회")
    public record PointBalanceResult(
            Long userId,
            String userName,
            Long balance
    ){
        public static PointBalanceResult of(User user, Point point){
            return new PointBalanceResult(
                    user.getUserId(),
                    user.getName(),
                    point.getBalance()
            );
        }
    }
}
