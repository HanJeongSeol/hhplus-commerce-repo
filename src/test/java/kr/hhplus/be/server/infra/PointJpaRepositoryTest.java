package kr.hhplus.be.server.infra;

import kr.hhplus.be.server.infra.point.PointJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@EnableJpaRepositories(basePackages = "kr.hhplus.be.server.infra.point")  // 리포지토리 위치 명시
public class PointJpaRepositoryTest {
    @Autowired
    private PointJpaRepository pointJpaRepository;

    @Test
    void jpaRepositoryShouldBeLoaded() {
        assertThat(pointJpaRepository).isNotNull();
    }
}
