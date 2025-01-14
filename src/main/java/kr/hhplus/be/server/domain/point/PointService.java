package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.support.constant.ErrorCode;
import kr.hhplus.be.server.support.exception.BusinessException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PointService {

    private final PointRepository pointRepository;
    @Transactional
    public Point chargePoint(User user, Long amount){
        Point point = pointRepository.findByUserWithLock(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_POINT_NOT_FOUND));

        point.charge(amount);
        return pointRepository.save(point);
    }

    @Transactional
    public Point getPoint(User user){
        return pointRepository.findByUserWithLock(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_POINT_NOT_FOUND));
    }

    @Transactional
    public Point usePoint(User user, Long amount){
        Point point = pointRepository.findByUserWithLock(user)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_POINT_NOT_FOUND));

        point.use(amount);
        return pointRepository.save(point);
    }
}
