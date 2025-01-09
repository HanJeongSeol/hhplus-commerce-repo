package kr.hhplus.be.server.infra.point;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.point.PointRepository;
import kr.hhplus.be.server.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class PointRepositoryImpl implements PointRepository {

    private final PointJpaRepository pointJpaRepository;
    @Override
    public Point save(Point point) {
        return pointJpaRepository.save(point);
    }

    @Override
    public Optional<Point> findByUser(User user) {
        return pointJpaRepository.findByUser(user);
    }

    @Override
    public Optional<Point> findByUserWithLock(User user) {
        return pointJpaRepository.findByUserIdWithLock(user);
    }
}
