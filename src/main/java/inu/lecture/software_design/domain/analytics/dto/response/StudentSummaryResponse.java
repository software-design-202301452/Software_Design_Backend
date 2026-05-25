package inu.lecture.software_design.domain.analytics.dto.response;

import inu.lecture.software_design.domain.analytics.entity.StudentLearningSummary;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class StudentSummaryResponse {
    private Long studentId;
    private String studentName;
    private Integer grade;
    private Integer classNum;
    private Integer studentNumber;
    private Double overallAverage;
    private Long gradeCount;
    private Long feedbackCount;
    private Long counselingCount;
    private LocalDateTime lastSyncedAt;

    public static StudentSummaryResponse from(StudentLearningSummary s) {
        return StudentSummaryResponse.builder()
                .studentId(s.getStudentId())
                .studentName(s.getStudentName())
                .grade(s.getGrade())
                .classNum(s.getClassNum())
                .studentNumber(s.getStudentNumber())
                .overallAverage(s.getOverallAverage())
                .gradeCount(s.getGradeCount())
                .feedbackCount(s.getFeedbackCount())
                .counselingCount(s.getCounselingCount())
                .lastSyncedAt(s.getLastSyncedAt())
                .build();
    }
}
