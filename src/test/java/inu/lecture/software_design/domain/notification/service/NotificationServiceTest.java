package inu.lecture.software_design.domain.notification.service;

import inu.lecture.software_design.domain.feedback.entity.Feedback;
import inu.lecture.software_design.domain.feedback.entity.FeedbackType;
import inu.lecture.software_design.domain.grade.entity.Grade;
import inu.lecture.software_design.domain.notification.dto.NotificationResponse;
import inu.lecture.software_design.domain.notification.dto.UnreadCountResponse;
import inu.lecture.software_design.domain.notification.entity.Notification;
import inu.lecture.software_design.domain.notification.entity.NotificationType;
import inu.lecture.software_design.domain.notification.repository.NotificationRepository;
import inu.lecture.software_design.domain.parent.entity.Parent;
import inu.lecture.software_design.domain.parent.repository.ParentRepository;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.subject.entity.Subject;
import inu.lecture.software_design.domain.teacher.entity.Teacher;
import inu.lecture.software_design.domain.user.enums.UserRole;
import inu.lecture.software_design.global.exception.CustomException;
import inu.lecture.software_design.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CEW-100: 알림 서비스 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @InjectMocks
    private NotificationService notificationService;

    @Mock private NotificationRepository notificationRepository;
    @Mock private ParentRepository parentRepository;

    private Student mockStudent;
    private Teacher mockTeacher;
    private Parent mockParent;
    private Grade mockGrade;
    private Feedback mockFeedback;

    @BeforeEach
    void setUp() throws Exception {
        mockStudent = Student.of("student1", "pw", "s@test.com", "김학생", 2, 3, 15, null, null);
        setId(mockStudent, 1L);

        mockTeacher = Teacher.of("teacher1", "pw", "t@test.com", "박교사", "수학", null);
        setId(mockTeacher, 1L);

        mockParent = Parent.of("parent1", "pw", "p@test.com", "김부모", mockStudent, null);
        setId(mockParent, 1L);

        Subject mockSubject = Subject.of("수학", "수학 과목");
        setId(mockSubject, 1L);

        mockGrade = Grade.of(mockStudent, mockSubject, mockTeacher, 2024, 1, 85.0, 100.0, 85.0, null);
        setId(mockGrade, 1L);

        mockFeedback = Feedback.of(mockStudent, mockTeacher, FeedbackType.GRADE, "테스트 피드백");
        setId(mockFeedback, 1L);
    }

    @Test
    void 성적_알림_생성_학생과_학부모에게_발송() {
        // given
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));
        when(parentRepository.findByStudent(mockStudent)).thenReturn(List.of(mockParent));

        // when
        notificationService.notifyGradeUpdated(mockStudent, mockGrade);

        // then - 학생 1건 + 학부모 1건 = 2건 저장
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void 피드백_등록_알림_생성() {
        // given
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        notificationService.notifyFeedbackCreated(mockStudent, mockFeedback);

        // then - 학생에게만 1건
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void 피드백_공개_알림_학생과_학부모에게_발송() {
        // given
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));
        when(parentRepository.findByStudent(mockStudent)).thenReturn(List.of(mockParent));

        // when
        notificationService.notifyFeedbackPublished(mockStudent, mockFeedback);

        // then - 학생 1건 + 학부모 1건
        verify(notificationRepository, times(2)).save(any(Notification.class));
    }

    @Test
    void 상담_알림_생성_교사에게_발송() {
        // given
        when(notificationRepository.save(any(Notification.class))).thenAnswer(inv -> inv.getArgument(0));

        // when
        notificationService.notifyCounselingUpdated(mockTeacher, mockStudent, 1L);

        // then
        verify(notificationRepository, times(1)).save(any(Notification.class));
    }

    @Test
    void 내_알림_목록_조회() throws Exception {
        // given
        Notification n = buildNotification("student1", UserRole.STUDENT, NotificationType.GRADE_UPDATED, "성적 알림", 1L);
        when(notificationRepository.findByReceiverUsernameAndReceiverRoleOrderByCreatedAtDesc("student1", UserRole.STUDENT))
                .thenReturn(List.of(n));

        // when
        List<NotificationResponse> result = notificationService.getMyNotifications("student1", UserRole.STUDENT);

        // then
        assertThat(result).hasSize(1);
    }

    @Test
    void 미읽음_수_조회() {
        // given
        when(notificationRepository.countByReceiverUsernameAndReceiverRoleAndIsReadFalse("student1", UserRole.STUDENT))
                .thenReturn(3L);

        // when
        UnreadCountResponse result = notificationService.getUnreadCount("student1", UserRole.STUDENT);

        // then
        assertThat(result.getCount()).isEqualTo(3L);
    }

    @Test
    void 알림_읽음_처리_성공() throws Exception {
        // given
        Notification n = buildNotification("student1", UserRole.STUDENT, NotificationType.GRADE_UPDATED, "성적 알림", 1L);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));

        // when
        notificationService.markAsRead(1L, "student1");

        // then
        assertThat(n.isRead()).isTrue();
    }

    @Test
    void 다른_사용자_알림_읽음_처리_예외() throws Exception {
        // given
        Notification n = buildNotification("student1", UserRole.STUDENT, NotificationType.GRADE_UPDATED, "성적 알림", 1L);
        when(notificationRepository.findById(1L)).thenReturn(Optional.of(n));

        // when & then
        assertThatThrownBy(() -> notificationService.markAsRead(1L, "other_user"))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.FORBIDDEN));
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

    private Notification buildNotification(String username, UserRole role,
                                            NotificationType type, String message, Long refId) throws Exception {
        Notification n = Notification.of(username, role, type, message, refId);
        setId(n, refId);
        return n;
    }
}
