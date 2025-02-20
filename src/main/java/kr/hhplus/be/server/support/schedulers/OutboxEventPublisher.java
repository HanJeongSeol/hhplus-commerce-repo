package kr.hhplus.be.server.support.schedulers;

import kr.hhplus.be.server.domain.payment.PaymentOutboxEvent;
import kr.hhplus.be.server.domain.payment.PaymentOutboxEventRepository;
import kr.hhplus.be.server.infra.kafka.payment.PaymentKafkaProducer;
import kr.hhplus.be.server.support.constant.PaymentEventStatus;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class OutboxEventPublisher {
    private final PaymentOutboxEventRepository outboxEventRepository;
    private final PaymentKafkaProducer kafkaProducer;

    @Scheduled(fixedDelay = 10000)
    public void publishEvent(){
        List<PaymentOutboxEvent> events = outboxEventRepository.findByStatus(PaymentEventStatus.UNSENT);
        for (PaymentOutboxEvent event : events) {
            try {
                String key = String.valueOf(event.getId());
                kafkaProducer.sendMessage(event.getEventType(), key, event.getPayload());
                log.info("OutboxEvent {} 전송 성공", event.getId());
            } catch (Exception e) {
                log.error("OutboxEvent {} 전송 실패", event.getId(), e);
            }
        }
    }
}
