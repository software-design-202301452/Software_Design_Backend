package inu.lecture.software_design.global.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CEW-114: 상담 등록 이벤트 (Kafka 메시지)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CounselingCreatedEvent {
    public static final String TOPIC = "counseling-events";

    private Long counselingId;
    private Long studentId;
    private String studentName;
}
