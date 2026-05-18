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
import inu.lecture.software_design.domain.subject.entity.Subject;
import inu.lecture.software_design.domain.teacher.entity.Teacher;
import inu.lecture.software_design.global.exception.CustomException;
import inu.lecture.software_design.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * CEW-101: 보고서 생성 로직 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @InjectMocks
    private ReportService reportService;

    @Mock private StudentRepository studentRepository;
    @Mock private GradeRepository gradeRepository;
    @Mock private CounselingRepository counselingRepository;
    @Mock private FeedbackRepository feedbackRepository;

    private Student mockStudent;
    private Teacher mockTeacher;

    @BeforeEach
    void setUp() throws Exception {
        mockStudent = Student.of("student1", "pw", "s@test.com", "김학생", 2, 3, 15, null, null);
        setId(mockStudent, 1L);

        mockTeacher = Teacher.of("teacher1", "pw", "t@test.com", "박교사", "수학", null);
        setId(mockTeacher, 1L);
    }

    // ─── 성적 보고서 ────────────────────────────────────────────────────────────

    @Test
    void 성적_보고서_생성_성공() throws Exception {
        // given
        Subject subject = Subject.of("수학", "수학 과목");
        setId(subject, 1L);
        Grade grade = Grade.of(mockStudent, subject, mockTeacher, 2024, 1, 90.0, 100.0, 85.0, null);
        setId(grade, 1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(mockStudent));
        when(gradeRepository.findByStudentOrderByYearDescSemesterAsc(mockStudent)).thenReturn(List.of(grade));

        // when
        GradeReportResponse response = reportService.getGradeReport(1L);

        // then
        assertThat(response.getStudentId()).isEqualTo(1L);
        assertThat(response.getStudentName()).isEqualTo("김학생");
        assertThat(response.getGrades()).hasSize(1);
        assertThat(response.getOverallAverage()).isEqualTo(85.0);
        assertThat(response.getSubjectAverages()).containsKey("수학");
    }

    @Test
    void 성적_없는_학생_보고서_생성() {
        // given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(mockStudent));
        when(gradeRepository.findByStudentOrderByYearDescSemesterAsc(mockStudent)).thenReturn(List.of());

        // when
        GradeReportResponse response = reportService.getGradeReport(1L);

        // then
        assertThat(response.getGrades()).isEmpty();
        assertThat(response.getOverallAverage()).isEqualTo(0.0);
    }

    // ─── 상담 보고서 ────────────────────────────────────────────────────────────

    @Test
    void 상담_보고서_생성_성공() throws Exception {
        // given
        Counseling counseling = Counseling.of(mockStudent, mockTeacher,
                LocalDate.of(2024, 3, 10), "학습 태도 상담", "복습 권장", LocalDate.of(2024, 4, 10));
        setId(counseling, 1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(mockStudent));
        when(counselingRepository.findByStudentOrderByCounselingDateDesc(mockStudent)).thenReturn(List.of(counseling));

        // when
        CounselingReportResponse response = reportService.getCounselingReport(1L);

        // then
        assertThat(response.getStudentName()).isEqualTo("김학생");
        assertThat(response.getTotalCount()).isEqualTo(1);
        assertThat(response.getCounselings()).hasSize(1);
        assertThat(response.getCounselings().get(0).getTeacherName()).isEqualTo("박교사");
    }

    // ─── 피드백 보고서 ──────────────────────────────────────────────────────────

    @Test
    void 피드백_보고서_생성_성공() throws Exception {
        // given
        Feedback feedback = Feedback.of(mockStudent, mockTeacher, FeedbackType.GRADE, "성실한 학생입니다.");
        setId(feedback, 1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(mockStudent));
        when(feedbackRepository.findByFilter(eq(1L), isNull(), isNull(), isNull(), isNull()))
                .thenReturn(List.of(feedback));

        // when
        FeedbackReportResponse response = reportService.getFeedbackReport(1L);

        // then
        assertThat(response.getStudentName()).isEqualTo("김학생");
        assertThat(response.getTotalCount()).isEqualTo(1);
        assertThat(response.getTypeCountMap()).containsKey(FeedbackType.GRADE.name());
        // 모든 FeedbackType이 초기화되어 있어야 함
        for (FeedbackType type : FeedbackType.values()) {
            assertThat(response.getTypeCountMap()).containsKey(type.name());
        }
    }

    // ─── 공통 예외 처리 ─────────────────────────────────────────────────────────

    @Test
    void 존재하지_않는_학생_성적_보고서_예외() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportService.getGradeReport(999L))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.STUDENT_NOT_FOUND));
    }

    @Test
    void 존재하지_않는_학생_상담_보고서_예외() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportService.getCounselingReport(999L))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.STUDENT_NOT_FOUND));
    }

    @Test
    void 존재하지_않는_학생_피드백_보고서_예외() {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reportService.getFeedbackReport(999L))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.STUDENT_NOT_FOUND));
    }

    // ─── 헬퍼 메서드 ───────────────────────────────────────────────────────────

    private void setId(Object entity, Long id) throws Exception {
        Field field = getField(entity.getClass(), "id");
        field.setAccessible(true);
        field.set(entity, id);
    }

    private Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) return getField(clazz.getSuperclass(), fieldName);
            throw e;
        }
    }
}
