package kr.hhplus.be.server.application.point;

import kr.hhplus.be.server.application.point.request.PointCommand;
import kr.hhplus.be.server.application.point.response.PointResult;
import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointService;
import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PointFacade {
    private final PointService pointService;
    private final UserService userService;

    /**
     * 포인트 충전
     */
    public PointResult.PointChargeResult chargePoint(PointCommand.PointCharge command){
        // 사용자 조회
        User user = userService.getUserById(command.userId());
        // 포인트 충전
        Point point = pointService.chargePoint(command.userId(), command.amount());

        // 결과 반한
        return PointResult.PointChargeResult.of(user, point, command.amount());

    }

    /**
     * 포인트 조회
     */
    public PointResult.PointBalanceResult getPointBalance(PointCommand.PointBalance command) {
        System.out.println("Facade userId : " + command.userId());

        // 사용자 조회
        User user = userService.getUserById(command.userId());

        // 포인트 조회
        Point point = pointService.getPoint(command.userId());

        return PointResult.PointBalanceResult.of(user, point);
    }
}
