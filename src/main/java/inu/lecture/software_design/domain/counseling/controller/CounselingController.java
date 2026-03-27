package inu.lecture.software_design.domain.counseling.controller;

import inu.lecture.software_design.domain.counseling.dto.request.CreateCounselingRequest;
import inu.lecture.software_design.domain.counseling.dto.request.UpdateCounselingRequest;
import inu.lecture.software_design.domain.counseling.dto.response.CounselingResponse;
import inu.lecture.software_design.domain.counseling.service.CounselingService;
import inu.lecture.software_design.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 교사 전용 상담 관리 컨트롤러
 * SecurityConfig에 의해 TEACHER 역할만 접근 가능 (/api/v1/counselings/**)
 */
@RestController
@RequestMapping("/api/v1/counselings")
@RequiredArgsConstructor
public class CounselingController {

    private final CounselingService counselingService;

    /**
     * CEW-45: 상담 날짜/내용/다음 계획 등록
     * POST /api/v1/counselings
     */
    @PostMapping
    public ResponseEntity<ApiResponse<CounselingResponse>> createCounseling(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateCounselingRequest request) {
        CounselingResponse response = counselingService.createCounseling(userDetails.getUsername(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("상담이 등록되었습니다.", response));
    }

    /**
     * CEW-46: 상담 수정
     * PUT /api/v1/counselings/{counselingId}
     */
    @PutMapping("/{counselingId}")
    public ResponseEntity<ApiResponse<CounselingResponse>> updateCounseling(
            @PathVariable Long counselingId,
            @Valid @RequestBody UpdateCounselingRequest request) {
        CounselingResponse response = counselingService.updateCounseling(counselingId, request);
        return ResponseEntity.ok(ApiResponse.success("상담이 수정되었습니다.", response));
    }

    /**
     * CEW-47: 상담 삭제
     * DELETE /api/v1/counselings/{counselingId}
     */
    @DeleteMapping("/{counselingId}")
    public ResponseEntity<ApiResponse<Void>> deleteCounseling(
            @PathVariable Long counselingId) {
        counselingService.deleteCounseling(counselingId);
        return ResponseEntity.ok(ApiResponse.success("상담이 삭제되었습니다."));
    }

    /**
     * CEW-48: 특정 학생 상담 이력 조회
     * GET /api/v1/counselings/student/{studentId}
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<CounselingResponse>>> getCounselingsByStudent(
            @PathVariable Long studentId) {
        List<CounselingResponse> response = counselingService.getCounselingsByStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * CEW-49: 공유된 상담 기록 조회 (다른 교사도 열람 가능)
     * GET /api/v1/counselings/shared
     */
    @GetMapping("/shared")
    public ResponseEntity<ApiResponse<List<CounselingResponse>>> getSharedCounselings() {
        List<CounselingResponse> response = counselingService.getSharedCounselings();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * CEW-49: 상담 공유 설정
     * PATCH /api/v1/counselings/{counselingId}/share
     */
    @PatchMapping("/{counselingId}/share")
    public ResponseEntity<ApiResponse<CounselingResponse>> shareCounseling(
            @PathVariable Long counselingId) {
        CounselingResponse response = counselingService.shareCounseling(counselingId);
        return ResponseEntity.ok(ApiResponse.success("상담이 공유되었습니다.", response));
    }

    /**
     * CEW-49: 상담 공유 해제
     * PATCH /api/v1/counselings/{counselingId}/unshare
     */
    @PatchMapping("/{counselingId}/unshare")
    public ResponseEntity<ApiResponse<CounselingResponse>> unshareCounseling(
            @PathVariable Long counselingId) {
        CounselingResponse response = counselingService.unshareCounseling(counselingId);
        return ResponseEntity.ok(ApiResponse.success("상담 공유가 해제되었습니다.", response));
    }
}
