package inu.lecture.software_design.global.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

/**
 * CEW-114: Kafka 토픽 설정
 * ProducerFactory/KafkaTemplate은 Spring Boot 자동 설정에 위임하여
 * application.yml의 SASL 등 모든 설정이 적용되도록 한다.
 */
@Configuration
public class KafkaConfig {

    @Bean
    public NewTopic gradeEventsTopic() {
        return TopicBuilder.name("grade-events").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic feedbackEventsTopic() {
        return TopicBuilder.name("feedback-events").partitions(3).replicas(1).build();
    }

    @Bean
    public NewTopic counselingEventsTopic() {
        return TopicBuilder.name("counseling-events").partitions(3).replicas(1).build();
    }
}
