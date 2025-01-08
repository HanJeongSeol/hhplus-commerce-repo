package kr.hhplus.be.server.domain.point;

import kr.hhplus.be.server.domain.user.User;
import kr.hhplus.be.server.domain.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@DisplayName("포인트 서비스 동시성 테스트")
class PointServiceConcurrencyUnitTest {

    @Autowired
    private PointService pointService;

    @Autowired
    private UserRepository userRepository;

    // ApplicationContext 주입
    @Autowired
    private ApplicationContext applicationContext;
    private User testUser;

    private static final int THREAD_COUNT = 10;
    private static final long CHARGE_AMOUNT = 1000L;
    private static final Logger log = LoggerFactory.getLogger(PointServiceConcurrencyUnitTest.class);
    @BeforeEach
    @Transactional
    void setUp() {

        Point point = Point.builder()
                .balance(1000L)
                .build();

        testUser = User.builder()
                .name("테스트 사용자")
                .point(point)
                .build();


        testUser = userRepository.save(testUser);
    }

    @Test
    @DisplayName("동시에 여러 요청이 들어와도 포인트가 정확하게 충전되어야 한다")
    void concurrentChargePoint() throws InterruptedException {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch startLatch = new CountDownLatch(1);
        CountDownLatch endLatch = new CountDownLatch(THREAD_COUNT);

        // when
        for (int i = 0; i < THREAD_COUNT; i++) {
            executorService.submit(() -> {
                try {
                    startLatch.await();

                    // 각 트랜잭션을 완전히 독립적으로 실행
                    TransactionTemplate transactionTemplate = new TransactionTemplate(
                            applicationContext.getBean(PlatformTransactionManager.class)
                    );

                    transactionTemplate.execute(status -> {
                        Point updatedPoint = pointService.chargePoint(testUser, CHARGE_AMOUNT);
                        log.info("조회된 포인트: {}", updatedPoint.getBalance());
                        return null;
                    });
                } catch (Exception e) {
                    log.error("Error during point charge: ", e);
                } finally {
                    endLatch.countDown();
                }
            });
        }

        startLatch.countDown();
        endLatch.await(10, TimeUnit.SECONDS);

        // then
        Point finalPoint = pointService.getPoint(testUser);
        log.info("최종 포인트: {}", finalPoint.getBalance());
        assertThat(finalPoint.getBalance()).isEqualTo(CHARGE_AMOUNT * THREAD_COUNT + 1000);

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
    }

    @Test
    @DisplayName("동시에 충전과 조회 요청이 들어와도 충전된 포인트가 정확하게 조회되어야 한다")
    void concurrentChargeAndGetPoint() throws InterruptedException {
        // given
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_COUNT);
        CountDownLatch chargeLatch = new CountDownLatch(1);  // 충전 완료 시점을 알기 위한 래치
        CountDownLatch endLatch = new CountDownLatch(2);     // 충전과 조회 완료를 기다리는 래치


        // when
        // 포인트 충전 스레드
        executorService.submit(() -> {
            try {
                TransactionTemplate transactionTemplate = new TransactionTemplate(
                        applicationContext.getBean(PlatformTransactionManager.class)
                );

                transactionTemplate.execute(status -> {
                    pointService.chargePoint(testUser, CHARGE_AMOUNT);
                    log.info("포인트 충전 완료: {}", CHARGE_AMOUNT);
                    return null;
                });
            } catch (Exception e) {
                log.error("충전 중 에러 발생", e);
            } finally {
                chargeLatch.countDown();
                endLatch.countDown();
            }
        });


       // 포인트 조회 쓰레드
        executorService.submit(() -> {
            try {
                Thread.sleep(100);  // 충전이 먼저 시작되도록 약간의 지연
                TransactionTemplate transactionTemplate = new TransactionTemplate(
                        applicationContext.getBean(PlatformTransactionManager.class)
                );

                transactionTemplate.execute(status -> {
                    Point point = pointService.getPoint(testUser);
                    log.info("조회된 포인트: {}", point.getBalance());
                    // 충전이 완료된 시점의 포인트가 조회되어야 함
                    assertThat(point.getBalance()).isEqualTo(CHARGE_AMOUNT + 1000);
                    return null;
                });
            } catch (Exception e) {
                log.error("조회 중 에러 발생", e);
            } finally {
                endLatch.countDown();
            }
        });

        // 모든 작업 완료 대기
        endLatch.await(10, TimeUnit.SECONDS);
        Point finalPoint = pointService.getPoint(testUser);
        assertThat(finalPoint.getBalance()).isEqualTo(1000L + CHARGE_AMOUNT);

        executorService.shutdown();
        executorService.awaitTermination(5, TimeUnit.SECONDS);
    }

}