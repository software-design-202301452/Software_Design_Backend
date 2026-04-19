package inu.lecture.software_design.domain.report.dto.response;

import inu.lecture.software_design.domain.grade.entity.GradeLevel;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * CEW-63: 성적 분석 보고서 DTO
 */
@Getter
@Builder
public class GradeReportResponse {

    private Long studentId;
    private String studentName;
    private Integer grade;
    private Integer classNum;
    private Integer studentNumber;
    private LocalDate generatedAt;

    private List<GradeItemDto> grades;
    private Map<String, Double> subjectAverages;
    private Double overallAverage;

    @Getter
    @Builder
    public static class GradeItemDto {
        private String subjectName;
        private Integer year;
        private Integer semester;
        private Double score;
        private Double totalScore;
        private Double average;
        private GradeLevel gradeLevel;
    }
}
