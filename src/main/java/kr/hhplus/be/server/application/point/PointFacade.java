package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import kr.hhplus.be.server.interfaces.dto.point.PointChargeRequest;
import kr.hhplus.be.server.interfaces.dto.point.PointChargeResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointFacade {
    private final PointService pointService;
    private final UserService userService;

    @Transactional
    public PointChargeResult chargePoint(PointChargeCommand request) {
        User user = userService.getUserById(request.userId());
        Point point = pointService.chargePoint(user, request.amount());

        return new PointChargeResult(user, point, request.amount());
    }

    public PointBalanceResult getPointBalance(Long userId) {
        User user = userService.getUserById(userId);
        Point point = pointService.getPoint(user);

        return new PointBalanceResult(user, point);
    }
}
