package inu.lecture.software_design.domain.subject.controller;

import inu.lecture.software_design.domain.subject.dto.request.CreateSubjectRequest;
import inu.lecture.software_design.domain.subject.dto.response.SubjectResponse;
import inu.lecture.software_design.domain.subject.service.SubjectService;
import inu.lecture.software_design.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 교사 전용 과목 관리 컨트롤러
 * SecurityConfig 에 의해 TEACHER 역할만 접근 가능 (/api/v1/subjects/**)
 */
@RestController
@RequestMapping("/api/v1/subjects")
@RequiredArgsConstructor
public class SubjectController {

    private final SubjectService subjectService;

    /**
     * 과목 생성 (성적 입력 시 과목 선택에 필요)
     * POST /api/v1/subjects
     */
    @PostMapping
    public ResponseEntity<ApiResponse<SubjectResponse>> createSubject(
            @Valid @RequestBody CreateSubjectRequest request) {
        SubjectResponse response = subjectService.createSubject(request);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("과목이 생성되었습니다.", response));
    }

    /**
     * 전체 과목 목록 조회
     * GET /api/v1/subjects
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<SubjectResponse>>> getAllSubjects() {
        List<SubjectResponse> response = subjectService.getAllSubjects();
        return ResponseEntity.ok(ApiResponse.success(response));
    }
}
