package inu.lecture.software_design.global.kafka.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * CEW-114: 성적 등록 이벤트 (Kafka 메시지)
 */
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeCreatedEvent {
    public static final String TOPIC = "grade-events";

    private Long studentId;
    private String studentName;
    private Long subjectId;
    private String subjectName;
    private Integer year;
    private Integer semester;
    private Double average;
}
