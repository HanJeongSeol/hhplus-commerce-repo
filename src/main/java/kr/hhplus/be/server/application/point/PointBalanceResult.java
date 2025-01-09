package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.dto.point.PointBalanceResponse;

public record PointBalanceResult(
        User user,
        Point point
) {
    public PointBalanceResponse toResponse() {
        return new PointBalanceResponse(
                user.getUserId(),
                user.getName(),
                point.getBalance()
        );
    }
}
