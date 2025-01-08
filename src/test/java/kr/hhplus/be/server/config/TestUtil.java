package kr.hhplus.be.server.config;

import kr.hhplus.be.server.domain.point.Point;
import kr.hhplus.be.server.domain.user.User;

public abstract class TestUtil {

    /**
     * 사용자 생성 및 초기 포인트 지급 후 연결
     */
    public static User createTestUser(){
        User user = User.builder()
                .userId(1L)
                .name("설한정")
                .build();

        user.initialPoint();

        return user;
    }

    public static Point createTestPoint(){
        return Point.builder()
                .pointId(1L)
                .balance(0L)
                .build();
    }
}
