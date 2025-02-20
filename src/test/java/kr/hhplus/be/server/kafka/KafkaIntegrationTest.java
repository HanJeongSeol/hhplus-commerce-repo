package kr.hhplus.be.server.kafka;

import kr.hhplus.be.server.infra.kafka.payment.PaymentKafkaProducer;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
@ActiveProfiles("local")
public class KafkaIntegrationTest {

    @Autowired
    private PaymentKafkaProducer producer;

    /**
     * Docker의 외부 리스너(19092,19093,19094)를 통해 KafkaConsumer를 생성 -> 외부 포트 설정 확인
     * Consumer는 test-topic을 구독하여 메시지 수신 여부를 확인
     *  -> 중간에 오류 발생했던 부분 확인 필요
     */
    private KafkaConsumer<String, String> createConsumer() {
        Properties props = new Properties();
        props.put("bootstrap.servers", "localhost:19092,localhost:19093,localhost:19094");
        props.put("group.id", "integration-test-group");
        props.put("enable.auto.commit", "true");
        props.put("auto.commit.interval.ms", "1000");
        props.put("key.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        props.put("value.deserializer", "org.apache.kafka.common.serialization.StringDeserializer");
        // 테스트 시점 이전의 모든 메시지를 읽기 위해 earliest 사용
        props.put("auto.offset.reset", "earliest");
        return new KafkaConsumer<>(props);
    }

    /**
     * @TestConfiguration 내부에서 KafkaAdmin으로 test-topic을 자동 생성
     */
    @TestConfiguration
    static class KafkaTestConfig {
        @Bean
        public NewTopic testTopic() {
            // 토픽 생성 시 파티션과 복제 개수는 클러스터 상황에 맞게 조정합니다.
            return TopicBuilder.name("test-topic")
                    .partitions(3)
                    .replicas(1)
                    .build();
        }
    }

    @Test
    public void 직접_생성한_KafkaConsumer로_메시지가_test_topic에_등록되었는지_확인() {
        String topic = "test-topic";
        String message = "Test Kafka Message";

        // KafkaConsumer 생성 및 구독
        KafkaConsumer<String, String> consumer = createConsumer();
        consumer.subscribe(Collections.singletonList(topic));

        // PaymentKafkaProducer를 이용해 실제 메시지 발행
        producer.sendMessage(topic, message);

        boolean messageFound = false;
        long timeout = System.currentTimeMillis() + 10000; // 최대 10초 대기

        // poll()을 통해 메시지 수신 여부 확인
        while (System.currentTimeMillis() < timeout) {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofMillis(500));
            for (ConsumerRecord<String, String> record : records) {
                if (record.value().equals(message)) {
                    messageFound = true;
                    break;
                }
            }
            if (messageFound) {
                break;
            }
        }
        consumer.close();

        // 메시지가 도착했는지 검증 (Kafka-UI에서서 실제 발행 확인 -> 대시보드 좌측 Topic의 Message 탭 확인해서 발행 시간 확인해야 함)
        assertTrue(messageFound, "Kafka에 메시지가 등록되지 않았습니다.");
    }
}