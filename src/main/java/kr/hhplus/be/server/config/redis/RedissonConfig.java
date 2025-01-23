package kr.hhplus.be.server.config.redis;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
@Slf4j
public class RedissonConfig {
    private static final String REDISSON_HOST_PREFIX = "redis://";

    @Bean
    public RedissonClient redissonClient() {
        try {
            Config config = new Config();
            config.useSingleServer().setAddress(REDISSON_HOST_PREFIX + "localhost:6379");
            return Redisson.create(config);
        } catch (Exception e) {
            log.error("레디스 연결 실패", e);
            throw new IllegalStateException("레디스 연결 실패", e);
        }
    }
}
