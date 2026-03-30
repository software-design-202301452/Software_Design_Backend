package inu.lecture.software_design.domain.notification.controller;

import inu.lecture.software_design.domain.notification.dto.NotificationResponse;
import inu.lecture.software_design.domain.notification.dto.UnreadCountResponse;
import inu.lecture.software_design.domain.notification.service.NotificationService;
import inu.lecture.software_design.domain.user.enums.UserRole;
import inu.lecture.software_design.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CEW-61/62: 알림 조회 및 읽음 처리 컨트롤러
 * anyRequest().authenticated() 적용 — 모든 로그인 사용자 접근 가능
 */
@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * CEW-61: 내 알림 목록 조회
     * GET /api/v1/notifications
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserRole role = extractRole(userDetails);
        List<NotificationResponse> response =
                notificationService.getMyNotifications(userDetails.getUsername(), role);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 미읽음 수 조회 (30초 폴링용)
     * GET /api/v1/notifications/unread-count
     */
    @GetMapping("/unread-count")
    public ResponseEntity<ApiResponse<UnreadCountResponse>> getUnreadCount(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserRole role = extractRole(userDetails);
        UnreadCountResponse response =
                notificationService.getUnreadCount(userDetails.getUsername(), role);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * CEW-62: 단건 읽음 처리
     * PATCH /api/v1/notifications/{id}/read
     */
    @PatchMapping("/{id}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(
            @PathVariable Long id,
            @AuthenticationPrincipal UserDetails userDetails) {
        notificationService.markAsRead(id, userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success("알림을 읽음 처리했습니다."));
    }

    /**
     * 전체 읽음 처리
     * PATCH /api/v1/notifications/read-all
     */
    @PatchMapping("/read-all")
    public ResponseEntity<ApiResponse<Void>> markAllAsRead(
            @AuthenticationPrincipal UserDetails userDetails) {
        UserRole role = extractRole(userDetails);
        notificationService.markAllAsRead(userDetails.getUsername(), role);
        return ResponseEntity.ok(ApiResponse.success("모든 알림을 읽음 처리했습니다."));
    }

    private UserRole extractRole(UserDetails userDetails) {
        return userDetails.getAuthorities().stream()
                .findFirst()
                .map(a -> UserRole.valueOf(a.getAuthority().replace("ROLE_", "")))
                .orElseThrow();
    }
}
