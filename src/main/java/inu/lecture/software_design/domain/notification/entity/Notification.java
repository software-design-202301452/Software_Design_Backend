package inu.lecture.software_design.domain.notification.entity;

import inu.lecture.software_design.domain.user.enums.UserRole;
import inu.lecture.software_design.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "notifications")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Notification extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * 수신자 username (Teacher/Student/Parent/User 중 하나의 username)
     * FK 없이 username으로 식별 - 어떤 엔티티 타입이든 수신 가능
     */
    @Column(nullable = false, length = 50)
    private String receiverUsername;

    /** 수신자 역할 (어느 테이블에서 찾을지 구분) */
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole receiverRole;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @Column(nullable = false, length = 500)
    private String message;

    /** 알림 대상 엔티티 ID (성적 ID, 피드백 ID 등) */
    private Long referenceId;

    @Column(name = "is_read", nullable = false)
    private boolean isRead;

    private Notification(String receiverUsername, UserRole receiverRole,
                         NotificationType notificationType, String message, Long referenceId) {
        this.receiverUsername = receiverUsername;
        this.receiverRole = receiverRole;
        this.notificationType = notificationType;
        this.message = message;
        this.referenceId = referenceId;
        this.isRead = false;
    }

    public static Notification of(String receiverUsername, UserRole receiverRole,
                                  NotificationType notificationType, String message, Long referenceId) {
        return new Notification(receiverUsername, receiverRole, notificationType, message, referenceId);
    }

    public void markAsRead() {
        this.isRead = true;
    }
}
