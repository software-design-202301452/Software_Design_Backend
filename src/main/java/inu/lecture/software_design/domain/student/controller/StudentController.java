package inu.lecture.software_design.domain.student.controller;

import inu.lecture.software_design.domain.student.dto.request.RegisterStudentRequest;
import inu.lecture.software_design.domain.student.dto.request.UpdateStudentRequest;
import inu.lecture.software_design.domain.student.dto.response.StudentDetailResponse;
import inu.lecture.software_design.domain.student.dto.response.StudentSummaryResponse;
import inu.lecture.software_design.domain.student.service.StudentService;
import inu.lecture.software_design.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 교사 전용 학생 관리 컨트롤러
 * SecurityConfig에 의해 TEACHER 역할만 접근 가능 (/api/v1/students/**)
 */
@RestController
@RequestMapping("/api/v1/students")
@RequiredArgsConstructor
public class StudentController {

    private final StudentService studentService;

    /**
     * CEW-25: 학생 기본 정보 등록
     * POST /api/v1/students
     */
    @PostMapping
    public ResponseEntity<ApiResponse<StudentSummaryResponse>> registerStudent(
            @Valid @RequestBody RegisterStudentRequest request) {
        StudentSummaryResponse response = studentService.registerStudent(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("학생이 등록되었습니다.", response));
    }

    /**
     * CEW-26: 학년/반/번호 기준으로 학생 목록 조회
     * GET /api/v1/students?grade=1&classNum=2&studentNumber=3
     * 파라미터 생략 시 전체 목록 반환
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<StudentSummaryResponse>>> getStudents(
            @RequestParam(required = false) Integer grade,
            @RequestParam(required = false) Integer classNum,
            @RequestParam(required = false) Integer studentNumber) {
        List<StudentSummaryResponse> response = studentService.getStudents(grade, classNum, studentNumber);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * CEW-27: 특정 학생의 기본 정보 + 학생부 목록 조회
     * GET /api/v1/students/{studentId}
     */
    @GetMapping("/{studentId}")
    public ResponseEntity<ApiResponse<StudentDetailResponse>> getStudentDetail(
            @PathVariable Long studentId) {
        StudentDetailResponse response = studentService.getStudentDetail(studentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * CEW-28: 학생 이름/학년/반/번호 등 기본 정보 수정
     * PUT /api/v1/students/{studentId}
     */
    @PutMapping("/{studentId}")
    public ResponseEntity<ApiResponse<Void>> updateStudent(
            @PathVariable Long studentId,
            @Valid @RequestBody UpdateStudentRequest request) {
        studentService.updateStudent(studentId, request);
        return ResponseEntity.ok(ApiResponse.success("학생 정보가 수정되었습니다."));
    }
}
