package inu.lecture.software_design.domain.dashboard.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * CEW-69: 교사 대시보드 응답 DTO
 */
@Getter
@Builder
public class TeacherDashboardResponse {

    private long totalStudents;
    private long totalGrades;
    private long unpublishedFeedbacks;
    private long recentCounselingsCount;

    private List<RecentCounseling> recentCounselings;
    private List<RecentFeedback> recentFeedbacks;
    private List<RecentGrade> recentGrades;

    @Getter
    @Builder
    public static class RecentCounseling {
        private Long id;
        private String studentName;
        private String teacherName;
        private LocalDate counselingDate;
        private String content;
    }

    @Getter
    @Builder
    public static class RecentFeedback {
        private Long id;
        private String studentName;
        private String teacherName;
        private String feedbackType;
        private String content;
        private boolean published;
        private LocalDateTime createdAt;
    }

    @Getter
    @Builder
    public static class RecentGrade {
        private Long id;
        private String studentName;
        private String subjectName;
        private Integer year;
        private Integer semester;
        private Double score;
        private String gradeLevel;
        private LocalDateTime createdAt;
    }
}
