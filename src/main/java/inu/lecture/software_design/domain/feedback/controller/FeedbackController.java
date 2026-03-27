package inu.lecture.software_design.domain.feedback.controller;

import inu.lecture.software_design.domain.feedback.dto.request.CreateFeedbackRequest;
import inu.lecture.software_design.domain.feedback.dto.request.UpdateFeedbackRequest;
import inu.lecture.software_design.domain.feedback.dto.response.FeedbackResponse;
import inu.lecture.software_design.domain.feedback.service.FeedbackService;
import inu.lecture.software_design.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 교사 전용 피드백 관리 컨트롤러
 * SecurityConfig에 의해 TEACHER 역할만 접근 가능 (/api/v1/feedbacks/**)
 */
@RestController
@RequestMapping("/api/v1/feedbacks")
@RequiredArgsConstructor
public class FeedbackController {

    private final FeedbackService feedbackService;

    /**
     * CEW-39: 학생 피드백 작성
     * CEW-42: FeedbackType 지정 (GRADE/BEHAVIOR/ATTENDANCE/ATTITUDE/GENERAL)
     * POST /api/v1/feedbacks
     */
    @PostMapping
    public ResponseEntity<ApiResponse<FeedbackResponse>> createFeedback(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateFeedbackRequest request) {
        FeedbackResponse response = feedbackService.createFeedback(userDetails.getUsername(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("피드백이 등록되었습니다.", response));
    }

    /**
     * CEW-40: 피드백 수정
     * PUT /api/v1/feedbacks/{feedbackId}
     */
    @PutMapping("/{feedbackId}")
    public ResponseEntity<ApiResponse<FeedbackResponse>> updateFeedback(
            @PathVariable Long feedbackId,
            @Valid @RequestBody UpdateFeedbackRequest request) {
        FeedbackResponse response = feedbackService.updateFeedback(feedbackId, request);
        return ResponseEntity.ok(ApiResponse.success("피드백이 수정되었습니다.", response));
    }

    /**
     * CEW-41: 피드백 삭제
     * DELETE /api/v1/feedbacks/{feedbackId}
     */
    @DeleteMapping("/{feedbackId}")
    public ResponseEntity<ApiResponse<Void>> deleteFeedback(
            @PathVariable Long feedbackId) {
        feedbackService.deleteFeedback(feedbackId);
        return ResponseEntity.ok(ApiResponse.success("피드백이 삭제되었습니다."));
    }

    /**
     * CEW-43: 피드백 공개 설정
     * PATCH /api/v1/feedbacks/{feedbackId}/publish
     */
    @PatchMapping("/{feedbackId}/publish")
    public ResponseEntity<ApiResponse<FeedbackResponse>> publishFeedback(
            @PathVariable Long feedbackId) {
        FeedbackResponse response = feedbackService.publishFeedback(feedbackId);
        return ResponseEntity.ok(ApiResponse.success("피드백이 공개되었습니다.", response));
    }

    /**
     * CEW-43: 피드백 비공개 설정
     * PATCH /api/v1/feedbacks/{feedbackId}/unpublish
     */
    @PatchMapping("/{feedbackId}/unpublish")
    public ResponseEntity<ApiResponse<FeedbackResponse>> unpublishFeedback(
            @PathVariable Long feedbackId) {
        FeedbackResponse response = feedbackService.unpublishFeedback(feedbackId);
        return ResponseEntity.ok(ApiResponse.success("피드백이 비공개로 설정되었습니다.", response));
    }

    /**
     * CEW-44: 피드백 필터 조회 (studentId, teacherId, from, to 선택)
     * GET /api/v1/feedbacks?studentId=&teacherId=&from=&to=
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getFeedbacks(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long teacherId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        List<FeedbackResponse> response = feedbackService.getFeedbacks(studentId, teacherId, from, to);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
