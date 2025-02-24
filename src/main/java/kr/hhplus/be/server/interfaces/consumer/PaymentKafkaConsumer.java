package kr.hhplus.be.server.interfaces.consumer;

import kr.hhplus.be.server.domain.payment.PaymentOutboxEventRepository;
import kr.hhplus.be.server.infra.kafka.payment.PaymentKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentKafkaConsumer {

    private final PaymentOutboxEventRepository outboxEventRepository;
    private final PaymentKafkaProducer producer;

    @KafkaListener(topics = "PaymentCompletedEvent", groupId = "my-comsumer-group")
    public void consumePaymentCompletedEvent(ConsumerRecord<String, String> record) {

        int retryCount = 0; // 재시도 횟수 카운트
        boolean isSuccess = false;

        String key = record.key(); // Producer가 보낸 key값 가져오기.
        String message = record.value();
        Long outboxEventId = Long.valueOf(key); // key를 Long 타입 변환
        log.info("셀프 컨슈머 수신: Key={}, Value={}", key, message);
        while (retryCount < 3 && !isSuccess) {
            try {
                // Outbox 이벤트 레코드를 찾아 상태를 SENT로 업데이트
                outboxEventRepository.findById(outboxEventId).ifPresent(outboxEvent -> {
                    outboxEvent.markAsSent();
                    outboxEventRepository.save(outboxEvent);
                    log.info("OutboxEvent {} 상태를 SENT로 업데이트", outboxEventId);
                });
                isSuccess = true;
            } catch (Exception e) {
                retryCount++;
                log.error("셀프 컨슈머 처리 실패. 메시지: {}", record.value(), e);
            }
        }
        // 재시도 3번 실패 시 DLT로 메시지 전송
        if(!isSuccess){
            log.error("OutboxEvent {} 업데이트 실패 후 3회 재시도: DLT로 메시지 전송", outboxEventId);
            producer.sendMessage("DeadLetterTopic", key, message);
        }
    }
}
