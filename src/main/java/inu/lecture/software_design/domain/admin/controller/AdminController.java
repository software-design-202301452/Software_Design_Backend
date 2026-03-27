package inu.lecture.software_design.domain.admin.controller;

import inu.lecture.software_design.domain.admin.dto.request.AdminCreateParentRequest;
import inu.lecture.software_design.domain.admin.dto.request.AdminCreateStudentRequest;
import inu.lecture.software_design.domain.admin.dto.request.AdminCreateTeacherRequest;
import inu.lecture.software_design.domain.admin.dto.response.AccountListResponse;
import inu.lecture.software_design.domain.admin.service.AdminService;
import inu.lecture.software_design.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    /**
     * 전체 계정 목록 조회
     * GET /api/v1/admin/accounts
     */
    @GetMapping("/accounts")
    public ResponseEntity<ApiResponse<AccountListResponse>> getAllAccounts() {
        AccountListResponse response = adminService.getAllAccounts();
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 교사 계정 생성 (teacherCode 없이 관리자가 직접 생성)
     * POST /api/v1/admin/accounts/teacher
     */
    @PostMapping("/accounts/teacher")
    public ResponseEntity<ApiResponse<Void>> createTeacher(
            @Valid @RequestBody AdminCreateTeacherRequest request) {
        adminService.createTeacher(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("교사 계정이 생성되었습니다."));
    }

    /**
     * 학생 계정 생성
     * POST /api/v1/admin/accounts/student
     */
    @PostMapping("/accounts/student")
    public ResponseEntity<ApiResponse<Void>> createStudent(
            @Valid @RequestBody AdminCreateStudentRequest request) {
        adminService.createStudent(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("학생 계정이 생성되었습니다."));
    }

    /**
     * 학부모 계정 생성
     * POST /api/v1/admin/accounts/parent
     */
    @PostMapping("/accounts/parent")
    public ResponseEntity<ApiResponse<Void>> createParent(
            @Valid @RequestBody AdminCreateParentRequest request) {
        adminService.createParent(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("학부모 계정이 생성되었습니다."));
    }
}
