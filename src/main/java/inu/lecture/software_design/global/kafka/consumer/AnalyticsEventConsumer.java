package inu.lecture.software_design.global.kafka.consumer;

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
import inu.lecture.software_design.global.kafka.event.CounselingCreatedEvent;
import inu.lecture.software_design.global.kafka.event.FeedbackPublishedEvent;
import inu.lecture.software_design.global.kafka.event.GradeCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.OptionalDouble;

/**
 * CEW-114: 분석 이벤트 Kafka 컨슈머
 * 이벤트 수신 후 분석 집계 테이블을 즉시 업데이트한다.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyticsEventConsumer {

    private final StudentLearningSummaryRepository studentSummaryRepository;
    private final SubjectGradeSummaryRepository subjectSummaryRepository;
    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final FeedbackRepository feedbackRepository;
    private final CounselingRepository counselingRepository;

    @KafkaListener(topics = GradeCreatedEvent.TOPIC, groupId = "analytics-group",
            containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void onGradeCreated(GradeCreatedEvent event) {
        log.info("[Kafka Consumer] 성적 이벤트 수신 - studentId: {}", event.getStudentId());
        try {
            updateStudentSummary(event.getStudentId());
            updateSubjectSummary(event.getSubjectId(), event.getSubjectName());
        } catch (Exception e) {
            log.error("[Kafka Consumer] 성적 이벤트 처리 실패: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = FeedbackPublishedEvent.TOPIC, groupId = "analytics-group",
            containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void onFeedbackPublished(FeedbackPublishedEvent event) {
        log.info("[Kafka Consumer] 피드백 이벤트 수신 - studentId: {}", event.getStudentId());
        try {
            updateStudentSummary(event.getStudentId());
        } catch (Exception e) {
            log.error("[Kafka Consumer] 피드백 이벤트 처리 실패: {}", e.getMessage(), e);
        }
    }

    @KafkaListener(topics = CounselingCreatedEvent.TOPIC, groupId = "analytics-group",
            containerFactory = "kafkaListenerContainerFactory")
    @Transactional
    public void onCounselingCreated(CounselingCreatedEvent event) {
        log.info("[Kafka Consumer] 상담 이벤트 수신 - studentId: {}", event.getStudentId());
        try {
            updateStudentSummary(event.getStudentId());
        } catch (Exception e) {
            log.error("[Kafka Consumer] 상담 이벤트 처리 실패: {}", e.getMessage(), e);
        }
    }

    private void updateStudentSummary(Long studentId) {
        studentRepository.findById(studentId).ifPresent(student -> {
            List<Grade> grades = gradeRepository.findByStudentOrderByYearDescSemesterAsc(student);
            OptionalDouble avg = grades.stream().mapToDouble(Grade::getAverage).average();
            double overallAverage = avg.isPresent() ? Math.round(avg.getAsDouble() * 10.0) / 10.0 : 0.0;
            long feedbackCount = feedbackRepository.countByStudentId(studentId);
            long counselingCount = counselingRepository.countByStudentId(studentId);

            studentSummaryRepository.findByStudentId(studentId)
                    .ifPresentOrElse(
                            s -> s.update(overallAverage, (long) grades.size(), feedbackCount, counselingCount),
                            () -> studentSummaryRepository.save(buildStudentSummary(
                                    student, overallAverage, (long) grades.size(), feedbackCount, counselingCount))
                    );
        });
    }

    private void updateSubjectSummary(Long subjectId, String subjectName) {
        List<Grade> grades = gradeRepository.findAll().stream()
                .filter(g -> g.getSubject().getId().equals(subjectId))
                .toList();

        if (grades.isEmpty()) return;

        double avg = Math.round(grades.stream().mapToDouble(Grade::getAverage).average().orElse(0.0) * 10.0) / 10.0;
        double max = Math.round(grades.stream().mapToDouble(Grade::getAverage).max().orElse(0.0) * 10.0) / 10.0;
        double min = Math.round(grades.stream().mapToDouble(Grade::getAverage).min().orElse(0.0) * 10.0) / 10.0;

        subjectSummaryRepository.findBySubjectId(subjectId)
                .ifPresentOrElse(
                        s -> s.update(avg, max, min, (long) grades.size()),
                        () -> subjectSummaryRepository.save(
                                SubjectGradeSummary.builder()
                                        .subjectId(subjectId)
                                        .subjectName(subjectName)
                                        .avgScore(avg).maxScore(max).minScore(min)
                                        .studentCount((long) grades.size())
                                        .lastSyncedAt(LocalDateTime.now())
                                        .build()
                        )
                );
    }

    private StudentLearningSummary buildStudentSummary(Student student, double overallAverage,
                                                        long gradeCount, long feedbackCount, long counselingCount) {
        return StudentLearningSummary.builder()
                .studentId(student.getId())
                .studentName(student.getName())
                .grade(student.getGrade())
                .classNum(student.getClassNum())
                .studentNumber(student.getStudentNumber())
                .overallAverage(overallAverage)
                .gradeCount(gradeCount)
                .feedbackCount(feedbackCount)
                .counselingCount(counselingCount)
                .lastSyncedAt(LocalDateTime.now())
                .build();
    }
}
