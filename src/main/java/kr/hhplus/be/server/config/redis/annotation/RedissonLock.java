package kr.hhplus.be.server.config.redis.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RedissonLock {
    String value(); // Lock의 이름
    long waitTime() default 10L;  // Lock 획득 시도 최대 시간
    long leaseTime() default 5L; // Lock 점유 최대 시간
}
