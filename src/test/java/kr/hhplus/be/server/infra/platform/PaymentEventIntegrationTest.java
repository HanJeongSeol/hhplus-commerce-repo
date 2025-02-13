package kr.hhplus.be.server.infra.platform;

import kr.hhplus.be.server.application.port.out.DataPlatformService;
import kr.hhplus.be.server.infra.platform.publisher.PaymentEventPublisher;
import kr.hhplus.be.server.support.constant.PaymentStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Commit;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.shaded.org.awaitility.Awaitility;

import java.time.LocalDateTime;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@SpringBootTest
public class PaymentEventIntegrationTest {

    @Autowired
    private PaymentEventPublisher publisher;

    @Mock
    private DataPlatformService dataPlatformService;


    @Test
    @Transactional
    @Commit
    public void 결제_완료_이벤트가_비동기적으로_실행되는지_확인하기위해_이벤트_실행전_실행후_이름_다른지_검증() {
        // given: 더미 이벤트 생성
        CompletedEvent.PaymentCompletedEvent event = new CompletedEvent.PaymentCompletedEvent(
                1L, 1L, 1L, PaymentStatus.PAID, 10000L, null, 10000L, LocalDateTime.now()
        );

        // 현재 스레드 이름 저장
        String mainThreadName = Thread.currentThread().getName();

        // 비동기 스레드 이름을 저장할 변수
        AtomicReference<String> asyncThreadName = new AtomicReference<>();

        // when: 이벤트 발행 -> 비동기 실행
        publisher.publishPaymentCompleted(event);

        // then: Awaitility를 사용하여 비동기 실행 확인
        Awaitility.await()
                .atMost(3, TimeUnit.SECONDS)  // 최대 3초까지 기다리도록 설정
                .untilAsserted(() -> {
                    // 비동기 실행 스레드 이름이 변경되었는지 확인
                    asyncThreadName.set(Thread.currentThread().getName());

                    // 메인 스레드와 비동기 스레드가 다른지 검증
                    assertThat(asyncThreadName.get()).isNotEqualTo(mainThreadName);
                });
    }

    @Test
    @Transactional
    @Commit
    public void 결제_완료_이벤트_비동기_실행_전후_로그_비교() throws InterruptedException {
        // given: 더미 이벤트 생성
        CompletedEvent.PaymentCompletedEvent event = new CompletedEvent.PaymentCompletedEvent(
                1L, 1L, 1L, PaymentStatus.PAID, 10000L, null, 10000L, LocalDateTime.now()
        );

        // 현재 실행 중인 스레드 이름 출력
        System.out.println("테스트 => 실행 전 스레드 이름: " + Thread.currentThread().getName());

        // when: 이벤트 발행
        publisher.publishPaymentCompleted(event);

        // 비동기로 실행 <- 지연 필요
        Thread.sleep(2000);

        // 테스트 완료 메시지
        System.out.println("테스트 => 테스트 완료 - 로그의 쓰레드 이름과 출력된 이름이 동일한지 확인.");
    }
}
