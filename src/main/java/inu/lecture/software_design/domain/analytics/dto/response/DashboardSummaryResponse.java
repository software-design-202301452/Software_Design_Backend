package inu.lecture.software_design.domain.analytics.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class DashboardSummaryResponse {

    private Long totalStudents;

    /** 학년·반별 평균 */
    private List<ClassAverage> classAverages;

    /** 과목별 집계 (평균 내림차순) */
    private List<SubjectSummaryResponse> subjectSummaries;

    /** 상위 10명 학생 */
    private List<StudentSummaryResponse> topStudents;

    @Getter
    @Builder
    public static class ClassAverage {
        private Integer grade;
        private Integer classNum;
        private Double average;
    }
}
