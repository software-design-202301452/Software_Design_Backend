package inu.lecture.software_design.domain.analytics.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * CEW-112: 분석용 과목별 성적 집계 테이블
 */
@Entity
@Table(name = "analytics_subject_grade_summary")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class SubjectGradeSummary {

    @Id
    @Column(name = "subject_id")
    private Long subjectId;

    @Column(nullable = false, length = 100)
    private String subjectName;

    /** 과목 전체 평균 */
    @Column(nullable = false)
    private Double avgScore;

    /** 최고 점수 */
    @Column(nullable = false)
    private Double maxScore;

    /** 최저 점수 */
    @Column(nullable = false)
    private Double minScore;

    /** 해당 과목에 성적이 등록된 학생 수 */
    @Column(nullable = false)
    private Long studentCount;

    @Column(nullable = false)
    private LocalDateTime lastSyncedAt;

    public void update(Double avgScore, Double maxScore, Double minScore, Long studentCount) {
        this.avgScore = avgScore;
        this.maxScore = maxScore;
        this.minScore = minScore;
        this.studentCount = studentCount;
        this.lastSyncedAt = LocalDateTime.now();
    }
}
