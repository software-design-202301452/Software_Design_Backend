package inu.lecture.software_design.domain.notification.service;

import inu.lecture.software_design.domain.feedback.entity.Feedback;
import inu.lecture.software_design.domain.grade.entity.Grade;
import inu.lecture.software_design.domain.notification.dto.NotificationResponse;
import inu.lecture.software_design.domain.notification.dto.UnreadCountResponse;
import inu.lecture.software_design.domain.notification.entity.Notification;
import inu.lecture.software_design.domain.notification.entity.NotificationType;
import inu.lecture.software_design.domain.notification.repository.NotificationRepository;
import inu.lecture.software_design.domain.parent.repository.ParentRepository;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.teacher.entity.Teacher;
import inu.lecture.software_design.domain.user.enums.UserRole;
import inu.lecture.software_design.global.exception.CustomException;
import inu.lecture.software_design.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final ParentRepository parentRepository;

    /**
     * CEW-57: 성적 변경 시 학생 + 학부모에게 알림 생성
     * CEW-60: 성적 업데이트 시 학생/학부모에게 알림 발송
     */
    @Transactional
    public void notifyGradeUpdated(Student student, Grade grade) {
        String msg = String.format("[성적 알림] %s 과목 성적이 업데이트되었습니다. (등급: %s)",
                grade.getSubject().getName(), grade.getGradeLevel().name());

        notificationRepository.save(Notification.of(
                student.getUsername(), UserRole.STUDENT,
                NotificationType.GRADE_UPDATED, msg, grade.getId()));

        parentRepository.findByStudent(student).forEach(parent ->
                notificationRepository.save(Notification.of(
                        parent.getUsername(), UserRole.PARENT,
                        NotificationType.GRADE_UPDATED, msg, grade.getId())));

        log.info("성적 알림 생성 - student: {}", student.getUsername());
    }

    /**
     * CEW-58: 피드백 등록 시 알림 생성
     */
    @Transactional
    public void notifyFeedbackCreated(Student student, Feedback feedback) {
        String msg = "[피드백 알림] 새 피드백이 등록되었습니다.";

        notificationRepository.save(Notification.of(
                student.getUsername(), UserRole.STUDENT,
                NotificationType.FEEDBACK_PUBLISHED, msg, feedback.getId()));

        log.info("피드백 등록 알림 생성 - student: {}", student.getUsername());
    }

    /**
     * CEW-58+60: 피드백 공개 시 학생 + 학부모에게 알림 발송
     */
    @Transactional
    public void notifyFeedbackPublished(Student student, Feedback feedback) {
        String msg = "[피드백 공개] 선생님의 피드백이 공개되었습니다. 확인해보세요.";

        notificationRepository.save(Notification.of(
                student.getUsername(), UserRole.STUDENT,
                NotificationType.FEEDBACK_PUBLISHED, msg, feedback.getId()));

        parentRepository.findByStudent(student).forEach(parent ->
                notificationRepository.save(Notification.of(
                        parent.getUsername(), UserRole.PARENT,
                        NotificationType.FEEDBACK_PUBLISHED, msg, feedback.getId())));

        log.info("피드백 공개 알림 생성 - student: {}", student.getUsername());
    }

    /**
     * CEW-59: 상담 내역 변경 시 관련 교사에게 알림 발송
     */
    @Transactional
    public void notifyCounselingUpdated(Teacher teacher, Student student, Long counselingId) {
        String msg = String.format("[상담 알림] %s 학생의 상담 내역이 등록/수정되었습니다.", student.getName());

        notificationRepository.save(Notification.of(
                teacher.getUsername(), UserRole.TEACHER,
                NotificationType.COUNSELING_UPDATED, msg, counselingId));

        log.info("상담 알림 생성 - teacher: {}, student: {}", teacher.getUsername(), student.getUsername());
    }

    /**
     * CEW-61: 내 알림 목록 조회
     */
    @Transactional(readOnly = true)
    public List<NotificationResponse> getMyNotifications(String username, UserRole role) {
        return notificationRepository
                .findByReceiverUsernameAndReceiverRoleOrderByCreatedAtDesc(username, role)
                .stream()
                .map(NotificationResponse::from)
                .toList();
    }

    /**
     * 미읽음 수 조회 (폴링용)
     */
    @Transactional(readOnly = true)
    public UnreadCountResponse getUnreadCount(String username, UserRole role) {
        long count = notificationRepository
                .countByReceiverUsernameAndReceiverRoleAndIsReadFalse(username, role);
        return new UnreadCountResponse(count);
    }

    /**
     * CEW-62: 단건 읽음 처리
     */
    @Transactional
    public void markAsRead(Long notificationId, String username) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));
        if (!notification.getReceiverUsername().equals(username)) {
            throw new CustomException(ErrorCode.FORBIDDEN);
        }
        notification.markAsRead();
        log.info("알림 읽음 처리 - notificationId: {}", notificationId);
    }

    /**
     * 전체 읽음 처리
     */
    @Transactional
    public void markAllAsRead(String username, UserRole role) {
        notificationRepository
                .findByReceiverUsernameAndReceiverRoleAndIsReadFalse(username, role)
                .forEach(Notification::markAsRead);
        log.info("전체 알림 읽음 처리 - username: {}", username);
    }
}
