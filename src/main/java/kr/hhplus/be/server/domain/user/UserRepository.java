package kr.hhplus.be.server.domain.user;

import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public interface UserRepository {
    User save(User user);

    Optional<User> findById(Long userId);
}
