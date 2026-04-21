package inu.lecture.software_design.domain.parent.controller;

import inu.lecture.software_design.domain.feedback.dto.response.FeedbackResponse;
import inu.lecture.software_design.domain.grade.dto.response.GradeResponse;
import inu.lecture.software_design.domain.parent.dto.response.MyStudentResponse;
import inu.lecture.software_design.domain.parent.service.ParentService;
import inu.lecture.software_design.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/parent")
@RequiredArgsConstructor
public class ParentController {

    private final ParentService parentService;

    /**
     * 학부모 본인이 연동된 자녀 정보 조회
     * GET /api/v1/parent/my-student
     */
    @GetMapping("/my-student")
    public ResponseEntity<ApiResponse<MyStudentResponse>> getMyStudent(
            @AuthenticationPrincipal UserDetails userDetails) {
        MyStudentResponse response = parentService.getMyStudent(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(response));
    }

    /**
     * 학부모가 연동된 자녀의 성적 조회
     * GET /api/v1/parent/student-grades
     */
    @GetMapping("/student-grades")
    public ResponseEntity<ApiResponse<List<GradeResponse>>> getStudentGrades(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<GradeResponse> grades = parentService.getStudentGrades(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(grades));
    }

    /**
     * 학부모가 연동된 자녀의 피드백 조회 (published만)
     * GET /api/v1/parent/student-feedbacks
     */
    @GetMapping("/student-feedbacks")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getStudentFeedbacks(
            @AuthenticationPrincipal UserDetails userDetails) {
        List<FeedbackResponse> feedbacks = parentService.getStudentFeedbacks(userDetails.getUsername());
        return ResponseEntity.ok(ApiResponse.success(feedbacks));
    }
}
