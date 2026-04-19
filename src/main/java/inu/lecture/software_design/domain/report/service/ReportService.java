package inu.lecture.software_design.domain.report.service;

import inu.lecture.software_design.domain.counseling.entity.Counseling;
import inu.lecture.software_design.domain.counseling.repository.CounselingRepository;
import inu.lecture.software_design.domain.feedback.entity.Feedback;
import inu.lecture.software_design.domain.feedback.entity.FeedbackType;
import inu.lecture.software_design.domain.feedback.repository.FeedbackRepository;
import inu.lecture.software_design.domain.grade.entity.Grade;
import inu.lecture.software_design.domain.grade.repository.GradeRepository;
import inu.lecture.software_design.domain.report.dto.response.CounselingReportResponse;
import inu.lecture.software_design.domain.report.dto.response.FeedbackReportResponse;
import inu.lecture.software_design.domain.report.dto.response.GradeReportResponse;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.student.repository.StudentRepository;
import inu.lecture.software_design.global.exception.CustomException;
import inu.lecture.software_design.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * CEW-63,64,65: 보고서 데이터 조회 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ReportService {

    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;
    private final CounselingRepository counselingRepository;
    private final FeedbackRepository feedbackRepository;

    /**
     * CEW-63: 성적 분석 보고서 생성
     */
    @Transactional(readOnly = true)
    public GradeReportResponse getGradeReport(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        List<Grade> grades = gradeRepository.findByStudentOrderByYearDescSemesterAsc(student);

        List<GradeReportResponse.GradeItemDto> gradeItems = grades.stream()
                .map(g -> GradeReportResponse.GradeItemDto.builder()
                        .subjectName(g.getSubject().getName())
                        .year(g.getYear())
                        .semester(g.getSemester())
                        .score(g.getScore())
                        .totalScore(g.getTotalScore())
                        .average(g.getAverage())
                        .gradeLevel(g.getGradeLevel())
                        .build())
                .toList();

        // 과목별 평균 계산
        Map<String, Double> subjectAverages = grades.stream()
                .collect(Collectors.groupingBy(
                        g -> g.getSubject().getName(),
                        Collectors.averagingDouble(Grade::getAverage)
                ));

        // 전체 평균 계산
        double overallAverage = grades.isEmpty() ? 0.0 :
                grades.stream().mapToDouble(Grade::getAverage).average().orElse(0.0);

        log.info("성적 보고서 생성 - studentId: {}", studentId);

        return GradeReportResponse.builder()
                .studentId(student.getId())
                .studentName(student.getName())
                .grade(student.getGrade())
                .classNum(student.getClassNum())
                .studentNumber(student.getStudentNumber())
                .generatedAt(LocalDate.now())
                .grades(gradeItems)
                .subjectAverages(subjectAverages)
                .overallAverage(overallAverage)
                .build();
    }

    /**
     * CEW-64: 상담 기록 보고서 생성
     */
    @Transactional(readOnly = true)
    public CounselingReportResponse getCounselingReport(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        List<Counseling> counselings = counselingRepository.findByStudentOrderByCounselingDateDesc(student);

        List<CounselingReportResponse.CounselingItemDto> counselingItems = counselings.stream()
                .map(c -> CounselingReportResponse.CounselingItemDto.builder()
                        .counselingDate(c.getCounselingDate())
                        .teacherName(c.getTeacher().getName())
                        .content(c.getContent())
                        .nextPlan(c.getNextPlan())
                        .nextCounselingDate(c.getNextCounselingDate())
                        .build())
                .toList();

        log.info("상담 보고서 생성 - studentId: {}", studentId);

        return CounselingReportResponse.builder()
                .studentId(student.getId())
                .studentName(student.getName())
                .generatedAt(LocalDate.now())
                .totalCount(counselings.size())
                .counselings(counselingItems)
                .build();
    }

    /**
     * CEW-65: 피드백 요약 보고서 생성
     */
    @Transactional(readOnly = true)
    public FeedbackReportResponse getFeedbackReport(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        List<Feedback> feedbacks = feedbackRepository.findByFilter(studentId, null, null, null);

        // 타입별 카운트 맵
        Map<String, Long> typeCountMap = feedbacks.stream()
                .collect(Collectors.groupingBy(
                        f -> f.getFeedbackType().name(),
                        Collectors.counting()
                ));

        // 모든 FeedbackType에 대해 0으로 초기화
        for (FeedbackType type : FeedbackType.values()) {
            typeCountMap.putIfAbsent(type.name(), 0L);
        }

        List<FeedbackReportResponse.FeedbackItemDto> feedbackItems = feedbacks.stream()
                .map(f -> FeedbackReportResponse.FeedbackItemDto.builder()
                        .feedbackType(f.getFeedbackType())
                        .teacherName(f.getTeacher().getName())
                        .content(f.getContent())
                        .published(f.isPublished())
                        .createdAt(f.getCreatedAt())
                        .build())
                .toList();

        log.info("피드백 보고서 생성 - studentId: {}", studentId);

        return FeedbackReportResponse.builder()
                .studentId(student.getId())
                .studentName(student.getName())
                .generatedAt(LocalDate.now())
                .totalCount(feedbacks.size())
                .typeCountMap(typeCountMap)
                .feedbacks(feedbackItems)
                .build();
    }
}
