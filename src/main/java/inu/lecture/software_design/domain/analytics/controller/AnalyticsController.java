package inu.lecture.software_design.domain.analytics.controller;

import inu.lecture.software_design.domain.analytics.dto.response.DashboardSummaryResponse;
import inu.lecture.software_design.domain.analytics.dto.response.StudentSummaryResponse;
import inu.lecture.software_design.domain.analytics.dto.response.StudentTrendResponse;
import inu.lecture.software_design.domain.analytics.dto.response.SubjectSummaryResponse;
import inu.lecture.software_design.domain.analytics.scheduler.AnalyticsEtlScheduler;
import inu.lecture.software_design.domain.analytics.service.AnalyticsService;
import inu.lecture.software_design.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * CEW-115: 학습 현황 집계 조회 API
 */
@RestController
@RequestMapping("/api/v1/analytics")
@RequiredArgsConstructor
public class AnalyticsController {

    private final AnalyticsService analyticsService;
    private final AnalyticsEtlScheduler etlScheduler;

    /** 교사용 전체 대시보드 집계 */
    @GetMapping("/dashboard")
    public ResponseEntity<ApiResponse<DashboardSummaryResponse>> getDashboard() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getDashboard()));
    }

    /** 학생별 학습 요약 */
    @GetMapping("/students/{studentId}/summary")
    public ResponseEntity<ApiResponse<StudentSummaryResponse>> getStudentSummary(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getStudentSummary(studentId)));
    }

    /** 학생 성적 추이 (운영 DB 직접 조회) */
    @GetMapping("/students/{studentId}/trend")
    public ResponseEntity<ApiResponse<StudentTrendResponse>> getStudentTrend(
            @PathVariable Long studentId) {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getStudentTrend(studentId)));
    }

    /** 과목별 집계 목록 */
    @GetMapping("/subjects")
    public ResponseEntity<ApiResponse<List<SubjectSummaryResponse>>> getSubjectSummaries() {
        return ResponseEntity.ok(ApiResponse.success(analyticsService.getAllSubjectSummaries()));
    }

    /** ETL 수동 트리거 (관리자/개발용) */
    @PostMapping("/etl/trigger")
    public ResponseEntity<ApiResponse<Void>> triggerEtl() {
        etlScheduler.runEtlNow();
        return ResponseEntity.ok(ApiResponse.success("ETL 실행 완료"));
    }
}
