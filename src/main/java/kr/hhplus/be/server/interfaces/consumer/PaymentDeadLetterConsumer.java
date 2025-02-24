package kr.hhplus.be.server.interfaces.consumer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentDeadLetterConsumer {

    @KafkaListener(topics = "DeadLetterTopic", groupId = "dead-letter-consumer-group")
    public void consumeDeadLetter(ConsumerRecord<String, String> record) {
        log.error("DLT 메시지 수신됨: Key={}, Value={}", record.key(), record.value());
        sendDeveloperNotification(record);
    }

    // 최종 로그 출력 확인 -> 나중에는 실패한 메시지를 처리하는 로직들을 구현하면 된다~~~~
    private void sendDeveloperNotification(ConsumerRecord<String, String> record) {
        log.error("개발자 알림 - Dead Letter 메시지 발생: Key={}, Value={}", record.key(), record.value());
    }
}
