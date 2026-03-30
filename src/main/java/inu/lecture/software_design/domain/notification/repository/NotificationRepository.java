package inu.lecture.software_design.domain.notification.repository;

import inu.lecture.software_design.domain.notification.entity.Notification;
import inu.lecture.software_design.domain.user.enums.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    /** CEW-61: 내 알림 목록 (최신순) */
    List<Notification> findByReceiverUsernameAndReceiverRoleOrderByCreatedAtDesc(
            String receiverUsername, UserRole receiverRole);

    /** 미읽음 알림 목록 */
    List<Notification> findByReceiverUsernameAndReceiverRoleAndIsReadFalse(
            String receiverUsername, UserRole receiverRole);

    /** 미읽음 수 (폴링용) */
    long countByReceiverUsernameAndReceiverRoleAndIsReadFalse(
            String receiverUsername, UserRole receiverRole);
}
