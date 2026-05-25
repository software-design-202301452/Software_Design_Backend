package inu.lecture.software_design.domain.analytics.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class StudentTrendResponse {
    private Long studentId;
    private String studentName;

    /** 성적 추이 데이터 (연도·학기별) */
    private List<GradePoint> grades;

    @Getter
    @Builder
    public static class GradePoint {
        private Integer year;
        private Integer semester;
        private String subjectName;
        private Double average;
        private String gradeLevel;
    }
}
