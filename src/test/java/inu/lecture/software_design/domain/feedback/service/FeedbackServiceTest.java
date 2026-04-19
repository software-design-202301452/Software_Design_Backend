package inu.lecture.software_design.domain.feedback.service;

import inu.lecture.software_design.domain.feedback.dto.request.CreateFeedbackRequest;
import inu.lecture.software_design.domain.feedback.dto.request.UpdateFeedbackRequest;
import inu.lecture.software_design.domain.feedback.dto.response.FeedbackResponse;
import inu.lecture.software_design.domain.feedback.entity.Feedback;
import inu.lecture.software_design.domain.feedback.entity.FeedbackType;
import inu.lecture.software_design.domain.feedback.repository.FeedbackRepository;
import inu.lecture.software_design.domain.notification.service.NotificationService;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.student.repository.StudentRepository;
import inu.lecture.software_design.domain.teacher.entity.Teacher;
import inu.lecture.software_design.domain.teacher.repository.TeacherRepository;
import inu.lecture.software_design.global.exception.CustomException;
import inu.lecture.software_design.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CEW-77: 피드백 서비스 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class FeedbackServiceTest {

    @InjectMocks
    private FeedbackService feedbackService;

    @Mock
    private FeedbackRepository feedbackRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private NotificationService notificationService;

    private Student mockStudent;
    private Teacher mockTeacher;
    private Feedback mockFeedback;

    @BeforeEach
    void setUp() throws Exception {
        mockStudent = Student.of("student1", "password", "student@test.com",
                "테스트학생", 2, 3, 15, "010-1234-5678", "서울시 강남구");
        setId(mockStudent, 1L);

        mockTeacher = Teacher.of("teacher1", "password", "teacher@test.com",
                "테스트교사", "수학", "010-9876-5432");
        setId(mockTeacher, 1L);

        mockFeedback = Feedback.of(mockStudent, mockTeacher, FeedbackType.GRADE, "성적이 향상되고 있습니다.");
        setId(mockFeedback, 1L);
    }

    @Test
    void 피드백_등록_성공() throws Exception {
        // given
        CreateFeedbackRequest request = createFeedbackRequest(1L, FeedbackType.GRADE, "성적이 향상되고 있습니다.");

        when(teacherRepository.findByUsername("teacher1")).thenReturn(Optional.of(mockTeacher));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(mockStudent));
        when(feedbackRepository.save(any(Feedback.class))).thenReturn(mockFeedback);
        doNothing().when(notificationService).notifyFeedbackCreated(any(), any());

        // when
        FeedbackResponse response = feedbackService.createFeedback("teacher1", request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getFeedbackType()).isEqualTo(FeedbackType.GRADE);
        assertThat(response.getStudentName()).isEqualTo("테스트학생");
        assertThat(response.isPublished()).isFalse();
        verify(feedbackRepository).save(any(Feedback.class));
        verify(notificationService).notifyFeedbackCreated(any(), any());
    }

    @Test
    void 피드백_공개_성공() {
        // given
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(mockFeedback));
        doNothing().when(notificationService).notifyFeedbackPublished(any(), any());

        // when
        FeedbackResponse response = feedbackService.publishFeedback(1L);

        // then
        assertThat(response.isPublished()).isTrue();
        verify(notificationService).notifyFeedbackPublished(any(), any());
    }

    @Test
    void 피드백_수정_성공() throws Exception {
        // given
        UpdateFeedbackRequest request = createUpdateFeedbackRequest(FeedbackType.BEHAVIOR, "행동이 개선되었습니다.");
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(mockFeedback));

        // when
        FeedbackResponse response = feedbackService.updateFeedback(1L, request);

        // then
        assertThat(response.getFeedbackType()).isEqualTo(FeedbackType.BEHAVIOR);
        assertThat(response.getContent()).isEqualTo("행동이 개선되었습니다.");
    }

    @Test
    void 존재하지_않는_피드백_조회_예외() {
        // given
        when(feedbackRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> feedbackService.updateFeedback(999L, new UpdateFeedbackRequest()))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.FEEDBACK_NOT_FOUND));
    }

    @Test
    void 피드백_삭제_성공() {
        // given
        when(feedbackRepository.findById(1L)).thenReturn(Optional.of(mockFeedback));

        // when
        feedbackService.deleteFeedback(1L);

        // then
        verify(feedbackRepository).delete(mockFeedback);
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

    private CreateFeedbackRequest createFeedbackRequest(Long studentId, FeedbackType type, String content)
            throws Exception {
        CreateFeedbackRequest req = new CreateFeedbackRequest();
        setField(req, "studentId", studentId);
        setField(req, "feedbackType", type);
        setField(req, "content", content);
        return req;
    }

    private UpdateFeedbackRequest createUpdateFeedbackRequest(FeedbackType type, String content)
            throws Exception {
        UpdateFeedbackRequest req = new UpdateFeedbackRequest();
        setField(req, "feedbackType", type);
        setField(req, "content", content);
        return req;
    }

    private void setField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}
