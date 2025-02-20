package kr.hhplus.be.server.infra.kafka.payment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;



    /**
     * 비동기적 처리를 위해 Future 객체를 CompletableFuture로 변환
     */
    private <T> CompletableFuture<T> toCompletableFuture(Future<T> future) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return future.get();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }
        });
    }

    /**
     * 지정한 토픽에 메시지 발행
     */
    public CompletableFuture<SendResult<String, String>> sendMessage(String topic, String key, String message) {
        Future<SendResult<String, String>> future = kafkaTemplate.send(topic, key, message);
        log.info("Kafka 메시지 전송 시도: Topic={}, Key={}, Payload={}", topic, key, message);
        return toCompletableFuture(future)
                .whenComplete((result, ex) -> {
                    if (ex != null) {
                        log.error("Kafka 메시지 전송 실패: Topic={}, Key={}, Payload={}", topic, key, message, ex);
                    } else {
                        log.info("Kafka 메시지 전송 성공: Topic={}, Key={}, Payload={}", topic, key, message);
                    }
                });
    }


}