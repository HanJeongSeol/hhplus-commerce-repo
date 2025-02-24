package kr.hhplus.be.server.interfaces.consumer;

import com.fasterxml.jackson.databind.ObjectMapper;
import kr.hhplus.be.server.application.payment.response.PaymentResult;
import kr.hhplus.be.server.infra.kafka.payment.PaymentKafkaProducer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class DataPlatformConsumer {
    private final ObjectMapper objectMapper;
    private final PaymentKafkaProducer producer; // DLT 전송에 사용

    @KafkaListener(topics = "PaymentCompletedEvent", groupId = "data-platform-consumer-group")
    public void consumePaymentEvent(ConsumerRecord<String, String> record) {
        String key = record.key();
        String message = record.value();
        log.info("DataPlatformConsumer 수신: Key={}, Value={}", key, message);

        PaymentResult.PaymentProcessResult event;
        try {
            event = objectMapper.readValue(message, PaymentResult.PaymentProcessResult.class);
        } catch (Exception e) {
            log.error("메시지 직렬화 실패: {}", message, e);
            return;
        }

        int retryCount = 0;
        boolean isSuccess = false;
        while (retryCount < 3 && !isSuccess) {
            try {
                // 실제 DataPlatform에 전송하는 로직 수행
                sendToDataPlatform(event);
                isSuccess = true;
            } catch (Exception e) {
                retryCount++;
                log.error("DataPlatform 전송 실패, retryCount: {} (PaymentId: {})", retryCount, event.paymentId(), e);
            }
        }

        // 3회 재시도 후에도 실패하면 Dead Letter Topic으로 메시지 전송
        if (!isSuccess) {
            log.error("DataPlatform 전송 실패 후 3회 재시도: Dead Letter Topic으로 전송 (PaymentId: {})", event.paymentId());
            producer.sendMessage("DeadLetterTopic", key, message);
        }
    }

    private void sendToDataPlatform(PaymentResult.PaymentProcessResult event) {
        // 성공 시 로그 확인
         log.info("DataPlatform에 결제정보 전송 완료: PaymentId={}, OrderId={}", event.paymentId(), event.orderId());

        // 테스트를 위한 강제 예외 발생 후 로그 확인
//        throw new RuntimeException("DLT 전송 확인을 위한 예외 발생");
    }
}
