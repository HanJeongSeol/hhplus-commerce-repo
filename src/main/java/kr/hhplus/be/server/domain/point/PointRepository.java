package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.user.User;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface PointRepository {
    Point save(Point point);

    Optional<Point> findByUser(User user);
    Optional<Point> findByUserWithLock(User user);

}
