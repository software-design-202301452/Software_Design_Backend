package inu.lecture.software_design.domain.analytics.service;

import inu.lecture.software_design.domain.analytics.dto.response.DashboardSummaryResponse;
import inu.lecture.software_design.domain.analytics.dto.response.DashboardSummaryResponse.ClassAverage;
import inu.lecture.software_design.domain.analytics.dto.response.StudentSummaryResponse;
import inu.lecture.software_design.domain.analytics.dto.response.StudentTrendResponse;
import inu.lecture.software_design.domain.analytics.dto.response.StudentTrendResponse.GradePoint;
import inu.lecture.software_design.domain.analytics.dto.response.SubjectSummaryResponse;
import inu.lecture.software_design.domain.analytics.entity.StudentLearningSummary;
import inu.lecture.software_design.domain.analytics.entity.SubjectGradeSummary;
import inu.lecture.software_design.domain.analytics.repository.StudentLearningSummaryRepository;
import inu.lecture.software_design.domain.analytics.repository.SubjectGradeSummaryRepository;
import inu.lecture.software_design.domain.grade.repository.GradeRepository;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.student.repository.StudentRepository;
import inu.lecture.software_design.global.exception.CustomException;
import inu.lecture.software_design.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * CEW-115: 학생별·과목별 학습 현황 집계 조회 서비스
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsService {

    private final StudentLearningSummaryRepository studentSummaryRepository;
    private final SubjectGradeSummaryRepository subjectSummaryRepository;
    private final StudentRepository studentRepository;
    private final GradeRepository gradeRepository;

    /** GET /api/v1/analytics/students/{studentId}/summary */
    @Transactional(readOnly = true)
    public StudentSummaryResponse getStudentSummary(Long studentId) {
        StudentLearningSummary summary = studentSummaryRepository.findByStudentId(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));
        return StudentSummaryResponse.from(summary);
    }

    /** GET /api/v1/analytics/dashboard */
    @Transactional(readOnly = true)
    public DashboardSummaryResponse getDashboard() {
        long totalStudents = studentRepository.count();

        List<ClassAverage> classAverages = studentSummaryRepository.findClassAverages()
                .stream()
                .map(row -> ClassAverage.builder()
                        .grade((Integer) row[0])
                        .classNum((Integer) row[1])
                        .average(Math.round((Double) row[2] * 10.0) / 10.0)
                        .build())
                .toList();

        List<SubjectSummaryResponse> subjectSummaries = subjectSummaryRepository
                .findAllByOrderByAvgScoreDesc()
                .stream()
                .map(SubjectSummaryResponse::from)
                .toList();

        List<StudentSummaryResponse> topStudents = studentSummaryRepository
                .findTop10ByOrderByOverallAverageDesc()
                .stream()
                .map(StudentSummaryResponse::from)
                .toList();

        return DashboardSummaryResponse.builder()
                .totalStudents(totalStudents)
                .classAverages(classAverages)
                .subjectSummaries(subjectSummaries)
                .topStudents(topStudents)
                .build();
    }

    /** GET /api/v1/analytics/students/{studentId}/trend — 운영 DB Grade 직접 조회 */
    @Transactional(readOnly = true)
    public StudentTrendResponse getStudentTrend(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        List<GradePoint> grades = gradeRepository
                .findByStudentOrderByYearDescSemesterAsc(student)
                .stream()
                .map(g -> GradePoint.builder()
                        .year(g.getYear())
                        .semester(g.getSemester())
                        .subjectName(g.getSubject().getName())
                        .average(g.getAverage())
                        .gradeLevel(g.getGradeLevel().name())
                        .build())
                .toList();

        return StudentTrendResponse.builder()
                .studentId(student.getId())
                .studentName(student.getName())
                .grades(grades)
                .build();
    }

    /** GET /api/v1/analytics/subjects */
    @Transactional(readOnly = true)
    public List<SubjectSummaryResponse> getAllSubjectSummaries() {
        return subjectSummaryRepository.findAllByOrderByAvgScoreDesc()
                .stream()
                .map(SubjectSummaryResponse::from)
                .toList();
    }
}
