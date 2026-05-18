package inu.lecture.software_design.integration;

import inu.lecture.software_design.domain.feedback.entity.Feedback;
import inu.lecture.software_design.domain.feedback.entity.FeedbackType;
import inu.lecture.software_design.domain.feedback.repository.FeedbackRepository;
import inu.lecture.software_design.domain.feedback.service.FeedbackService;
import inu.lecture.software_design.domain.grade.entity.Grade;
import inu.lecture.software_design.domain.grade.entity.GradeLevel;
import inu.lecture.software_design.domain.grade.repository.GradeRepository;
import inu.lecture.software_design.domain.grade.service.GradeService;
import inu.lecture.software_design.domain.notification.entity.Notification;
import inu.lecture.software_design.domain.notification.repository.NotificationRepository;
import inu.lecture.software_design.domain.notification.service.NotificationService;
import inu.lecture.software_design.domain.parent.entity.Parent;
import inu.lecture.software_design.domain.parent.repository.ParentRepository;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.student.repository.StudentRepository;
import inu.lecture.software_design.domain.subject.entity.Subject;
import inu.lecture.software_design.domain.subject.repository.SubjectRepository;
import inu.lecture.software_design.domain.teacher.entity.Teacher;
import inu.lecture.software_design.domain.teacher.repository.TeacherRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CEW-102: 통합 테스트 - 주요 플로우
 * 성적 등록 → 알림 발송, 피드백 공개 → 알림 발송 등 핵심 플로우를 검증합니다.
 */
@ExtendWith(MockitoExtension.class)
class MainFlowIntegrationTest {

    // ─── GradeService ──────────────────────────────────────────────────────────
    @InjectMocks
    private GradeService gradeService;

    @Mock private GradeRepository gradeRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private SubjectRepository subjectRepository;
    @Mock private TeacherRepository teacherRepository;

    // ─── NotificationService ───────────────────────────────────────────────────
    @Spy
    @InjectMocks
    private NotificationService notificationService;

    @Mock private NotificationRepository notificationRepository;
    @Mock private ParentRepository parentRepository;

    // ─── FeedbackService ───────────────────────────────────────────────────────
    @InjectMocks
    private FeedbackService feedbackService;

    @Mock private FeedbackRepository feedbackRepository;

    private Student mockStudent;
    private Teacher mockTeacher;
    private Subject mockSubject;
    private Parent mockParent;

    @BeforeEach
    void setUp() throws Exception {
        mockStudent = Student.of("student1", "pw", "s@test.com", "김학생", 2, 3, 15, null, null);
        setId(mockStudent, 1L);

        mockTeacher = Teacher.of("teacher1", "pw", "t@test.com", "박교사", "수학", null);
        setId(mockTeacher, 1L);

        mockSubject = Subject.of("수학", "수학 과목");
        setId(mockSubject, 1L);

        mockParent = Parent.of("parent1", "pw", "p@test.com", "김부모", mockStudent, null);
        setId(mockParent, 1L);

        // GradeService 에 NotificationService 주입
        injectField(gradeService, "notificationService", notificationService);
        // FeedbackService 에 NotificationService 주입
        injectField(feedbackService, "notificationService", notificationService);
    }

    // ─── 성적 등록 → 알림 발송 플로우 ─────────────────────────────────────────

    @Test
    void 성적_등록_후_학생과_학부모에게_알림이_발송된다() throws Exception {
        // given
        inu.lecture.software_design.domain.grade.dto.request.CreateGradeRequest req =
                new inu.lecture.software_design.domain.grade.dto.request.CreateGradeRequest();
        setField(req, "studentId", 1L);
        setField(req, "subjectId", 1L);
        setField(req, "year", 2024);
        setField(req, "semester", 1);
        setField(req, "score", 90.0);
        setField(req, "totalScore", 100.0);
        setField(req, "note", null);

        Grade savedGrade = Grade.of(mockStudent, mockSubject, mockTeacher, 2024, 1, 90.0, 100.0, 90.0, null);
        setId(savedGrade, 1L);

        when(teacherRepository.findByUsername("teacher1")).thenReturn(Optional.of(mockTeacher));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(mockStudent));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mockSubject));
        when(gradeRepository.existsByStudentAndSubjectAndYearAndSemester(any(), any(), any(), any())).thenReturn(false);
        when(gradeRepository.save(any(Grade.class))).thenReturn(savedGrade);
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));
        when(parentRepository.findByStudent(mockStudent)).thenReturn(List.of(mockParent));

        // when
        var response = gradeService.createGrade("teacher1", req);

        // then
        assertThat(response.getGradeLevel()).isEqualTo(GradeLevel.A);
        // 학생 + 학부모 각 1건씩 알림 발송 검증
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    // ─── 피드백 공개 → 알림 발송 플로우 ───────────────────────────────────────

    @Test
    void 피드백_공개_후_학생과_학부모에게_알림이_발송된다() throws Exception {
        // given
        Feedback feedback = Feedback.of(mockStudent, mockTeacher, FeedbackType.GRADE, "우수한 성적입니다.");
        setId(feedback, 1L);

        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(feedback));
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));
        when(parentRepository.findByStudent(mockStudent)).thenReturn(List.of(mockParent));

        // when
        feedbackService.publishFeedback(1L);

        // then
        assertThat(feedback.isPublished()).isTrue();
        verify(notificationRepository, times(2)).save(any(Notification.class));
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

    private void setField(Object obj, String fieldName, Object value) throws Exception {
        Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(obj, value);
    }

    private void injectField(Object target, String fieldName, Object value) {
        try {
            Field f = getField(target.getClass(), fieldName);
            f.setAccessible(true);
            f.set(target, value);
        } catch (Exception ignored) {
            // 필드가 없으면 무시 (이미 @InjectMocks 로 주입된 경우)
        }
    }
}
