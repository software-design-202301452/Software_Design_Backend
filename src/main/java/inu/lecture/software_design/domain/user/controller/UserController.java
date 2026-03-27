package inu.lecture.software_design.domain.user.controller;

import inu.lecture.software_design.domain.user.dto.request.UpdateMyInfoRequest;
import inu.lecture.software_design.domain.user.dto.request.UpdatePasswordRequest;
import inu.lecture.software_design.domain.user.dto.response.MyInfoResponse;
import inu.lecture.software_design.domain.user.service.UserService;
import inu.lecture.software_design.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    /**
     * 본인 정보 조회
     * GET /api/v1/users/me
     */
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<MyInfoResponse>> getMyInfo(
            @AuthenticationPrincipal UserDetails userDetails) {
        MyInfoResponse response = userService.getMyInfo(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 본인 정보 수정 (연락처, 부서 등)
     * PUT /api/v1/users/me
     */
    @PutMapping("/me")
    public ResponseEntity<ApiResponse<Void>> updateMyInfo(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdateMyInfoRequest request) {
        userService.updateMyInfo(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("정보가 수정되었습니다."));
    }

    /**
     * 비밀번호 변경
     * PUT /api/v1/users/me/password
     */
    @PutMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> updatePassword(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody UpdatePasswordRequest request) {
        userService.updatePassword(userDetails.getUsername(), request);
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 변경되었습니다."));
    }
}
