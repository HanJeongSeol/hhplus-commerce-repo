package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.interfaces.dto.point.PointChargeResponse;

public record PointChargeResult(
        User user,
        Point point,
        Long chargeAmount
) {
    public PointChargeResponse toResponse(){
        return new PointChargeResponse(
                user.getUserId(),
                user.getName(),
                point.getBalance(),
                chargeAmount,
                point.getUpdatedAt()
        );
    }
}
