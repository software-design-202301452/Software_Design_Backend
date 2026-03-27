package inu.lecture.software_design.domain.studentrecord.controller;

import inu.lecture.software_design.domain.studentrecord.dto.request.CreateStudentRecordRequest;
import inu.lecture.software_design.domain.studentrecord.dto.request.UpdateStudentRecordRequest;
import inu.lecture.software_design.domain.studentrecord.dto.response.StudentRecordResponse;
import inu.lecture.software_design.domain.studentrecord.service.StudentRecordService;
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
 * 교사 전용 학생부 관리 컨트롤러
 * SecurityConfig에 의해 TEACHER 역할만 접근 가능 (/api/v1/student-records/**)
 */
@RestController
@RequestMapping("/api/v1/student-records")
@RequiredArgsConstructor
public class StudentRecordController {

    private final StudentRecordService studentRecordService;

    /**
     * CEW-29: 출결/특기사항 등 학생부 항목 기록
     * POST /api/v1/student-records
     */
    @PostMapping
    public ResponseEntity<ApiResponse<StudentRecordResponse>> createStudentRecord(
            @AuthenticationPrincipal UserDetails userDetails,
            @Valid @RequestBody CreateStudentRecordRequest request) {
        StudentRecordResponse response = studentRecordService.createStudentRecord(
                userDetails.getUsername(), request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("학생부가 등록되었습니다.", response));
    }

    /**
     * CEW-30: 기존 학생부 항목 수정
     * PUT /api/v1/student-records/{recordId}
     */
    @PutMapping("/{recordId}")
    public ResponseEntity<ApiResponse<StudentRecordResponse>> updateStudentRecord(
            @PathVariable Long recordId,
            @Valid @RequestBody UpdateStudentRecordRequest request) {
        StudentRecordResponse response = studentRecordService.updateStudentRecord(recordId, request);
        return ResponseEntity.ok(ApiResponse.success("학생부가 수정되었습니다.", response));
    }

    /**
     * CEW-31: 교사가 학생별 학생부 전체 조회
     * GET /api/v1/student-records/student/{studentId}
     */
    @GetMapping("/student/{studentId}")
    public ResponseEntity<ApiResponse<List<StudentRecordResponse>>> getStudentRecords(
            @PathVariable Long studentId) {
        List<StudentRecordResponse> response = studentRecordService.getStudentRecords(studentId);
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
