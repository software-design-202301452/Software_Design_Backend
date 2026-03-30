package inu.lecture.software_design.domain.notification.dto;

import inu.lecture.software_design.domain.notification.entity.Notification;
import inu.lecture.software_design.domain.notification.entity.NotificationType;
import inu.lecture.software_design.domain.user.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationResponse {

    private Long id;
    private String receiverUsername;
    private UserRole receiverRole;
    private NotificationType notificationType;
    private String message;
    private Long referenceId;
    private boolean isRead;
    private LocalDateTime createdAt;

    public static NotificationResponse from(Notification n) {
        return NotificationResponse.builder()
                .id(n.getId())
                .receiverUsername(n.getReceiverUsername())
                .receiverRole(n.getReceiverRole())
                .notificationType(n.getNotificationType())
                .message(n.getMessage())
                .referenceId(n.getReferenceId())
                .isRead(n.isRead())
                .createdAt(n.getCreatedAt())
                .build();
    }
}
