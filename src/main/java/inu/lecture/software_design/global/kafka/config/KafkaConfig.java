package inu.lecture.software_design.global.kafka.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.Map;

/**
 * CEW-114: Kafka 토픽 및 프로듀서 설정
 * KafkaProperties를 통해 application.yml의 SASL 등 모든 설정을 자동으로 포함한다.
 */
@Configuration
public class KafkaConfig {

    @Bean
    public ProducerFactory<String, Object> producerFactory(KafkaProperties kafkaProperties) {
        Map<String, Object> props = kafkaProperties.buildProducerProperties(null);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate(ProducerFactory<String, Object> producerFactory) {
        return new KafkaTemplate<>(producerFactory);
    }

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
