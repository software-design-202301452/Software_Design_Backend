package inu.lecture.software_design.global.kafka.producer;

import inu.lecture.software_design.global.kafka.event.CounselingCreatedEvent;
import inu.lecture.software_design.global.kafka.event.FeedbackPublishedEvent;
import inu.lecture.software_design.global.kafka.event.GradeCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * CEW-114: 분석 이벤트 Kafka 프로듀서
 * 운영 도메인에서 발생한 변경 이벤트를 Kafka에 발행한다.
 * @Async로 비동기 처리하여 Kafka 연결 실패 시 메인 요청에 영향을 주지 않는다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsEventProducer {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Async
    public void sendGradeCreatedEvent(GradeCreatedEvent event) {
        try {
            kafkaTemplate.send(GradeCreatedEvent.TOPIC, String.valueOf(event.getStudentId()), event);
            log.info("[Kafka] 성적 이벤트 발행 - studentId: {}, subject: {}", event.getStudentId(), event.getSubjectName());
        } catch (Exception e) {
            log.warn("[Kafka] 성적 이벤트 발행 실패 (무시): {}", e.getMessage());
        }
    }

    @Async
    public void sendFeedbackPublishedEvent(FeedbackPublishedEvent event) {
        try {
            kafkaTemplate.send(FeedbackPublishedEvent.TOPIC, String.valueOf(event.getStudentId()), event);
            log.info("[Kafka] 피드백 이벤트 발행 - studentId: {}", event.getStudentId());
        } catch (Exception e) {
            log.warn("[Kafka] 피드백 이벤트 발행 실패 (무시): {}", e.getMessage());
        }
    }

    @Async
    public void sendCounselingCreatedEvent(CounselingCreatedEvent event) {
        try {
            kafkaTemplate.send(CounselingCreatedEvent.TOPIC, String.valueOf(event.getStudentId()), event);
            log.info("[Kafka] 상담 이벤트 발행 - studentId: {}", event.getStudentId());
        } catch (Exception e) {
            log.warn("[Kafka] 상담 이벤트 발행 실패 (무시): {}", e.getMessage());
        }
    }
}
