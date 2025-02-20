package kr.hhplus.be.server.interfaces.consumer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PaymentKafkaConsumer {


    /**
     * Kafka 토픽으로부터 메시지를 수신
     *
     * @param message 수신한 메시지 내용
     */
    @KafkaListener(topics = "test-topic", groupId = "my-comsumer-group")
    public void consume(String message){
        log.info("카프카 메시지 수신 : {}", message);
    }
}
