package inu.lecture.software_design.domain.analytics.scheduler;

import inu.lecture.software_design.domain.analytics.entity.StudentLearningSummary;
import inu.lecture.software_design.domain.analytics.entity.SubjectGradeSummary;
import inu.lecture.software_design.domain.analytics.repository.StudentLearningSummaryRepository;
import inu.lecture.software_design.domain.analytics.repository.SubjectGradeSummaryRepository;
import inu.lecture.software_design.domain.counseling.repository.CounselingRepository;
import inu.lecture.software_design.domain.feedback.repository.FeedbackRepository;
import inu.lecture.software_design.domain.grade.entity.Grade;
import inu.lecture.software_design.domain.grade.repository.GradeRepository;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.student.repository.StudentRepository;
import inu.lecture.software_design.domain.subject.entity.Subject;
import inu.lecture.software_design.domain.subject.repository.SubjectRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.OptionalDouble;
import java.util.stream.Collectors;

/**
 * CEW-113: 운영 DB → 분석 DB 주기적 ETL 스케줄러
 * 매시간 정각에 실행하여 집계 테이블을 갱신한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsEtlScheduler {

    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final GradeRepository gradeRepository;
    private final FeedbackRepository feedbackRepository;
    private final CounselingRepository counselingRepository;
    private final StudentLearningSummaryRepository studentSummaryRepository;
    private final SubjectGradeSummaryRepository subjectSummaryRepository;

    @Scheduled(cron = "${analytics.etl.cron:0 0 * * * *}")
    @Transactional
    public void runEtl() {
        log.info("[ETL] 분석 DB 집계 시작: {}", LocalDateTime.now());
        try {
            syncStudentLearningSummary();
            syncSubjectGradeSummary();
            log.info("[ETL] 분석 DB 집계 완료: {}", LocalDateTime.now());
        } catch (Exception e) {
            log.error("[ETL] 분석 DB 집계 실패: {}", e.getMessage(), e);
        }
    }

    /**
     * 학생별 학습 요약 집계 동기화
     */
    private void syncStudentLearningSummary() {
        List<Student> students = studentRepository.findAll();
        List<Grade> allGrades = gradeRepository.findAll();

        // 학생별 성적 그룹핑
        Map<Long, List<Grade>> gradesByStudent = allGrades.stream()
                .collect(Collectors.groupingBy(g -> g.getStudent().getId()));

        for (Student student : students) {
            List<Grade> grades = gradesByStudent.getOrDefault(student.getId(), List.of());

            OptionalDouble avg = grades.stream().mapToDouble(Grade::getAverage).average();
            double overallAverage = avg.isPresent() ? Math.round(avg.getAsDouble() * 10.0) / 10.0 : 0.0;

            long feedbackCount = feedbackRepository.countByStudentId(student.getId());
            long counselingCount = counselingRepository.countByStudentId(student.getId());

            studentSummaryRepository.findByStudentId(student.getId())
                    .ifPresentOrElse(
                            summary -> summary.update(overallAverage, (long) grades.size(), feedbackCount, counselingCount),
                            () -> studentSummaryRepository.save(
                                    StudentLearningSummary.builder()
                                            .studentId(student.getId())
                                            .studentName(student.getName())
                                            .grade(student.getGrade())
                                            .classNum(student.getClassNum())
                                            .studentNumber(student.getStudentNumber())
                                            .overallAverage(overallAverage)
                                            .gradeCount((long) grades.size())
                                            .feedbackCount(feedbackCount)
                                            .counselingCount(counselingCount)
                                            .lastSyncedAt(LocalDateTime.now())
                                            .build()
                            )
                    );
        }

        log.info("[ETL] 학생 학습 요약 동기화 완료 - {}명", students.size());
    }

    /**
     * 과목별 성적 집계 동기화
     */
    private void syncSubjectGradeSummary() {
        List<Subject> subjects = subjectRepository.findAll();
        List<Grade> allGrades = gradeRepository.findAll();

        Map<Long, List<Grade>> gradesBySubject = allGrades.stream()
                .collect(Collectors.groupingBy(g -> g.getSubject().getId()));

        for (Subject subject : subjects) {
            List<Grade> grades = gradesBySubject.getOrDefault(subject.getId(), List.of());

            if (grades.isEmpty()) continue;

            double avg = grades.stream().mapToDouble(Grade::getAverage).average().orElse(0.0);
            double max = grades.stream().mapToDouble(Grade::getAverage).max().orElse(0.0);
            double min = grades.stream().mapToDouble(Grade::getAverage).min().orElse(0.0);

            subjectSummaryRepository.findBySubjectId(subject.getId())
                    .ifPresentOrElse(
                            summary -> summary.update(
                                    Math.round(avg * 10.0) / 10.0,
                                    Math.round(max * 10.0) / 10.0,
                                    Math.round(min * 10.0) / 10.0,
                                    (long) grades.size()
                            ),
                            () -> subjectSummaryRepository.save(
                                    SubjectGradeSummary.builder()
                                            .subjectId(subject.getId())
                                            .subjectName(subject.getName())
                                            .avgScore(Math.round(avg * 10.0) / 10.0)
                                            .maxScore(Math.round(max * 10.0) / 10.0)
                                            .minScore(Math.round(min * 10.0) / 10.0)
                                            .studentCount((long) grades.size())
                                            .lastSyncedAt(LocalDateTime.now())
                                            .build()
                            )
                    );
        }

        log.info("[ETL] 과목 성적 요약 동기화 완료 - {}개 과목", subjects.size());
    }

    /** 수동 실행용 (즉시 ETL 트리거) */
    @Transactional
    public void runEtlNow() {
        log.info("[ETL] 수동 실행");
        runEtl();
    }
}
