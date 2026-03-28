package inu.lecture.software_design.domain.dashboard.controller;

import inu.lecture.software_design.domain.dashboard.dto.TeacherDashboardResponse;
import inu.lecture.software_design.domain.dashboard.service.DashboardService;
import inu.lecture.software_design.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * CEW-69: 교사용 대시보드 컨트롤러
 * GET /api/v1/dashboard/teacher
 */
@RestController
@RequestMapping("/api/v1/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    /**
     * CEW-69: 교사 대시보드 - 최근 상담/피드백/성적 현황 조회
     * GET /api/v1/dashboard/teacher
     */
    @GetMapping("/teacher")
    public ResponseEntity<ApiResponse<TeacherDashboardResponse>> getTeacherDashboard() {
        TeacherDashboardResponse response = dashboardService.getTeacherDashboard();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
