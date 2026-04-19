package inu.lecture.software_design.domain.report.controller;

import inu.lecture.software_design.domain.report.dto.response.CounselingReportResponse;
import inu.lecture.software_design.domain.report.dto.response.FeedbackReportResponse;
import inu.lecture.software_design.domain.report.dto.response.GradeReportResponse;
import inu.lecture.software_design.domain.report.service.ReportService;
import inu.lecture.software_design.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CEW-63,64,65: 보고서 API 컨트롤러
 * SecurityConfig 에 의해 TEACHER 역할만 접근 가능 (/api/v1/reports/**)
 */
@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    /**
     * CEW-63: 성적 분석 보고서 조회
     * GET /api/v1/reports/grades/{studentId}
     */
    @GetMapping("/grades/{studentId}")
    public ResponseEntity<ApiResponse<GradeReportResponse>> getGradeReport(
            @PathVariable Long studentId) {
        GradeReportResponse response = reportService.getGradeReport(studentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * CEW-64: 상담 기록 보고서 조회
     * GET /api/v1/reports/counselings/{studentId}
     */
    @GetMapping("/counselings/{studentId}")
    public ResponseEntity<ApiResponse<CounselingReportResponse>> getCounselingReport(
            @PathVariable Long studentId) {
        CounselingReportResponse response = reportService.getCounselingReport(studentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * CEW-65: 피드백 요약 보고서 조회
     * GET /api/v1/reports/feedbacks/{studentId}
     */
    @GetMapping("/feedbacks/{studentId}")
    public ResponseEntity<ApiResponse<FeedbackReportResponse>> getFeedbackReport(
            @PathVariable Long studentId) {
        FeedbackReportResponse response = reportService.getFeedbackReport(studentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
