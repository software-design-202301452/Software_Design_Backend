package inu.lecture.software_design.domain.analytics.dto.response;

import inu.lecture.software_design.domain.analytics.entity.SubjectGradeSummary;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SubjectSummaryResponse {
    private Long subjectId;
    private String subjectName;
    private Double avgScore;
    private Double maxScore;
    private Double minScore;
    private Long studentCount;

    public static SubjectSummaryResponse from(SubjectGradeSummary s) {
        return SubjectSummaryResponse.builder()
                .subjectId(s.getSubjectId())
                .subjectName(s.getSubjectName())
                .avgScore(s.getAvgScore())
                .maxScore(s.getMaxScore())
                .minScore(s.getMinScore())
                .studentCount(s.getStudentCount())
                .build();
    }
}
