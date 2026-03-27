package inu.lecture.software_design.domain.grade.controller;

import inu.lecture.software_design.domain.grade.dto.request.CreateGradeRequest;
import inu.lecture.software_design.domain.grade.dto.request.UpdateGradeRequest;
import inu.lecture.software_design.domain.grade.dto.response.GradeResponse;
import inu.lecture.software_design.domain.grade.service.GradeService;
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
 * 교사 전용 성적 관리 컨트롤러
 * SecurityConfig 에 의해 TEACHER 역할만 접근 가능 (/api/v1/grades/**)
 */
@RestController
@RequestMapping("/api/v1/grades")
@RequiredArgsConstructor
public class GradeController {

    private final GradeService gradeService;

    /**
     * CEW-32: 학생 성적 입력
     * CEW-35: 총점/평균 자동 계산
     * CEW-36: 등급 자동 산출
     * POST /api/v1/grades
     */
    @PostMapping
    public ResponseEntity<ApiResponse<GradeResponse>> createGrade(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateGradeRequest request) {
        GradeResponse response = gradeService.createGrade(userDetails.getUsername(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("성적이 등록되었습니다.", response));
    }

    /**
     * CEW-33: 이미 등록된 성적 수정 (평균/등급 재자동 계산)
     * PUT /api/v1/grades/{gradeId}
     */
    @PutMapping("/{gradeId}")
    public ResponseEntity<ApiResponse<GradeResponse>> updateGrade(
            @PathVariable Long gradeId,
            @Valid @RequestBody UpdateGradeRequest request) {
        GradeResponse response = gradeService.updateGrade(gradeId, request);
        return ResponseEntity.ok(ApiResponse.success("성적이 수정되었습니다.", response));
    }

    /**
     * CEW-34: 잘못 입력된 성적 삭제
     * DELETE /api/v1/grades/{gradeId}
     */
    @DeleteMapping("/{gradeId}")
    public ResponseEntity<ApiResponse<Void>> deleteGrade(
            @PathVariable Long gradeId) {
        gradeService.deleteGrade(gradeId);
        return ResponseEntity.ok(ApiResponse.success("성적이 삭제되었습니다."));
    }

    /**
     * CEW-37: 학생별 전체 성적 조회
     * GET /api/v1/grades/student/{studentId}
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<GradeResponse>>> getGradesByStudent(
            @PathVariable Long studentId) {
        List<GradeResponse> response = gradeService.getGradesByStudent(studentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * CEW-38: 과목별/학기별 성적 필터 조회
     * GET /api/v1/grades/student/{studentId}/filter?subjectId=&year=&semester=
     * 파라미터 생략 시 전체 조회
     */
    @GetMapping("/student/{studentId}/filter")
    public ResponseEntity<ApiResponse<List<GradeResponse>>> getGradesByFilter(
            @PathVariable Long studentId,
            @RequestParam(required = false) Long subjectId,
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer semester) {
        List<GradeResponse> response = gradeService.getGradesByFilter(studentId, subjectId, year, semester);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
