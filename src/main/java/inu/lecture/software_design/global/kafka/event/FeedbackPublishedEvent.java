package inu.lecture.software_design.global.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CEW-114: 피드백 공개 이벤트 (Kafka 메시지)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FeedbackPublishedEvent {
    public static final String TOPIC = "feedback-events";

    private Long feedbackId;
    private Long studentId;
    private String studentName;
}
