package inu.lecture.software_design.domain.report.dto.response;

import inu.lecture.software_design.domain.feedback.entity.FeedbackType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * CEW-65: 피드백 요약 보고서 DTO
 */
@Getter
@Builder
public class FeedbackReportResponse {

    private Long studentId;
    private String studentName;
    private LocalDate generatedAt;
    private Integer totalCount;

    private Map<String, Long> typeCountMap;
    private List<FeedbackItemDto> feedbacks;

    @Getter
    @Builder
    public static class FeedbackItemDto {
        private FeedbackType feedbackType;
        private String teacherName;
        private String content;
        private boolean published;
        private LocalDateTime createdAt;
    }
}
