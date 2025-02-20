package kr.hhplus.be.server.config.kafka;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * 프로젝트 실행 시 토픽 생성
 */
@Configuration
public class KafkaTopicConfig {
    @Bean
    public NewTopic paymentCompletedEventTopic() {
        return TopicBuilder.name("PaymentCompletedEvent")
                .partitions(3)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic deadLetterTopic() {
        return TopicBuilder.name("DeadLetterTopic")
                .partitions(1)
                .replicas(1)
                .build();
    }

    @Bean
    public NewTopic testTopic() {
        return TopicBuilder.name("test-topic")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
