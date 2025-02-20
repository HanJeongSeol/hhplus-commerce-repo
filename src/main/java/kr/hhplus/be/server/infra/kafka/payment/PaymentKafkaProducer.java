package kr.hhplus.be.server.infra.kafka.payment;

import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class PaymentKafkaProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;


    /**
     * 지정한 토픽에 메시지 발행.
     *
     * @param topic   메시지를 발행할 Kafka 토픽
     * @param message 발행할 메시지
     */
    public void sendMessage(String topic, String message){
        kafkaTemplate.send(topic, message);
        log.info("카프카 메시지 전달 : {}", message);
    }

}