package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.interfaces.dto.point.PointChargeRequest;

public record PointChargeCommand(
        Long userId,
        Long amount
) {
    public static PointChargeCommand from(PointChargeRequest request) {
        return new PointChargeCommand(
                request.userId(),
                request.amount()
        );
    }
}
