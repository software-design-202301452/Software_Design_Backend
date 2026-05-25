package inu.lecture.software_design.domain.analytics.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * CEW-112: 분석용 학생 학습 요약 집계 테이블
 * 운영 DB의 grades, feedbacks, counselings 데이터를 집계하여 저장
 */
@Entity
@Table(name = "analytics_student_learning_summary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class StudentLearningSummary {

    @Id
    @Column(name = "student_id")
    private Long studentId;

    @Column(nullable = false, length = 50)
    private String studentName;

    @Column(nullable = false)
    private Integer grade;

    @Column(nullable = false)
    private Integer classNum;

    @Column(nullable = false)
    private Integer studentNumber;

    /** 전체 성적 평균 */
    @Column(nullable = false)
    private Double overallAverage;

    /** 등록된 성적 수 */
    @Column(nullable = false)
    private Long gradeCount;

    /** 피드백 수 (공개 여부 무관) */
    @Column(nullable = false)
    private Long feedbackCount;

    /** 상담 횟수 */
    @Column(nullable = false)
    private Long counselingCount;

    /** 마지막 ETL 동기화 시각 */
    @Column(nullable = false)
    private LocalDateTime lastSyncedAt;

    public void update(Double overallAverage, Long gradeCount,
                       Long feedbackCount, Long counselingCount) {
        this.overallAverage = overallAverage;
        this.gradeCount = gradeCount;
        this.feedbackCount = feedbackCount;
        this.counselingCount = counselingCount;
        this.lastSyncedAt = LocalDateTime.now();
    }
}
