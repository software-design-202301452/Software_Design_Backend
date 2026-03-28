package inu.lecture.software_design.domain.dashboard.service;

import inu.lecture.software_design.domain.counseling.repository.CounselingRepository;
import inu.lecture.software_design.domain.dashboard.dto.TeacherDashboardResponse;
import inu.lecture.software_design.domain.dashboard.dto.TeacherDashboardResponse.RecentCounseling;
import inu.lecture.software_design.domain.dashboard.dto.TeacherDashboardResponse.RecentFeedback;
import inu.lecture.software_design.domain.dashboard.dto.TeacherDashboardResponse.RecentGrade;
import inu.lecture.software_design.domain.feedback.repository.FeedbackRepository;
import inu.lecture.software_design.domain.grade.repository.GradeRepository;
import inu.lecture.software_design.domain.student.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * CEW-69: 교사용 대시보드 서비스
 */
@Service
@RequiredArgsConstructor
public class DashboardService {

    private final StudentRepository studentRepository;
    private final CounselingRepository counselingRepository;
    private final FeedbackRepository feedbackRepository;
    private final GradeRepository gradeRepository;

    @Transactional(readOnly = true)
    public TeacherDashboardResponse getTeacherDashboard() {
        long totalStudents = studentRepository.count();
        long totalGrades = gradeRepository.count();
        long unpublishedFeedbacks = feedbackRepository.findAll()
                .stream().filter(f -> !f.isPublished()).count();

        var recentCounselings = counselingRepository.findTop5ByOrderByCounselingDateDesc()
                .stream()
                .map(c -> RecentCounseling.builder()
                        .id(c.getId())
                        .studentName(c.getStudent().getName())
                        .teacherName(c.getTeacher().getName())
                        .counselingDate(c.getCounselingDate())
                        .content(c.getContent())
                        .build())
                .toList();

        var recentFeedbacks = feedbackRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(f -> RecentFeedback.builder()
                        .id(f.getId())
                        .studentName(f.getStudent().getName())
                        .teacherName(f.getTeacher().getName())
                        .feedbackType(f.getFeedbackType().name())
                        .content(f.getContent())
                        .published(f.isPublished())
                        .createdAt(f.getCreatedAt())
                        .build())
                .toList();

        var recentGrades = gradeRepository.findTop5ByOrderByCreatedAtDesc()
                .stream()
                .map(g -> RecentGrade.builder()
                        .id(g.getId())
                        .studentName(g.getStudent().getName())
                        .subjectName(g.getSubject().getName())
                        .year(g.getYear())
                        .semester(g.getSemester())
                        .score(g.getScore())
                        .gradeLevel(g.getGradeLevel().name())
                        .createdAt(g.getCreatedAt())
                        .build())
                .toList();

        return TeacherDashboardResponse.builder()
                .totalStudents(totalStudents)
                .totalGrades(totalGrades)
                .unpublishedFeedbacks(unpublishedFeedbacks)
                .recentCounselingsCount((long) recentCounselings.size())
                .recentCounselings(recentCounselings)
                .recentFeedbacks(recentFeedbacks)
                .recentGrades(recentGrades)
                .build();
    }
}
