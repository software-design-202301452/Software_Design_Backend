package inu.lecture.software_design.domain.auth.controller;

import inu.lecture.software_design.domain.auth.dto.request.LoginRequest;
import inu.lecture.software_design.domain.auth.dto.request.SignupParentRequest;
import inu.lecture.software_design.domain.auth.dto.request.SignupStudentRequest;
import inu.lecture.software_design.domain.auth.dto.request.SignupTeacherRequest;
import inu.lecture.software_design.domain.auth.dto.response.LoginResponse;
import inu.lecture.software_design.domain.auth.dto.response.SignupStudentResponse;
import inu.lecture.software_design.domain.auth.service.AuthService;
import inu.lecture.software_design.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    /**
     * 교사 회원가입
     * POST /api/v1/auth/signup/teacher
     * Body: { username, password, name, email, teacherCode, department?, phone? }
     */
    @PostMapping("/signup/teacher")
    public ResponseEntity<ApiResponse<Void>> signupTeacher(
            @Valid @RequestBody SignupTeacherRequest request) {
        authService.signupTeacher(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("교사 회원가입이 완료되었습니다."));
    }

    /**
     * 학생 회원가입
     * POST /api/v1/auth/signup/student
     * Body: { username, password, name, email, grade, classNum, studentNumber, phone?, address? }
     * Response: linkCode 포함 (학부모 연동용 코드 - 학부모에게 전달 필요)
     */
    @PostMapping("/signup/student")
    public ResponseEntity<ApiResponse<SignupStudentResponse>> signupStudent(
            @Valid @RequestBody SignupStudentRequest request) {
        SignupStudentResponse response = authService.signupStudent(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("학생 회원가입이 완료되었습니다. 학부모 연동 코드를 안전하게 보관하세요.", response));
    }

    /**
     * 학부모 회원가입
     * POST /api/v1/auth/signup/parent
     * Body: { username, password, name, email, studentLinkCode, phone? }
     */
    @PostMapping("/signup/parent")
    public ResponseEntity<ApiResponse<Void>> signupParent(
            @Valid @RequestBody SignupParentRequest request) {
        authService.signupParent(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("학부모 회원가입이 완료되었습니다."));
    }

    /**
     * 로그인 (교사/학생/학부모 공통)
     * POST /api/v1/auth/login
     * Body: { username, password }
     * Response: accessToken, refreshToken, role 등
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(
            @Valid @RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        return ResponseEntity.ok(ApiResponse.success("로그인이 완료되었습니다.", response));
    }

    /**
     * 로그아웃 (인증된 사용자 공통)
     * POST /api/v1/auth/logout
     * - JWT는 stateless이므로 서버에서 토큰을 무효화하지 않음
     * - 클라이언트에서 토큰을 삭제하여 로그아웃 처리
     */
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout() {
        return ResponseEntity.ok(ApiResponse.success("로그아웃이 완료되었습니다. 클라이언트에서 토큰을 삭제해주세요."));
    }
}
