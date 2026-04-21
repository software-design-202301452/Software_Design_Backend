package inu.lecture.software_design.domain.student.controller;

import inu.lecture.software_design.domain.grade.dto.response.GradeResponse;
import inu.lecture.software_design.domain.grade.service.GradeService;
import inu.lecture.software_design.domain.feedback.dto.response.FeedbackResponse;
import inu.lecture.software_design.domain.feedback.service.FeedbackService;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.student.repository.StudentRepository;
import inu.lecture.software_design.domain.studentrecord.dto.response.StudentRecordResponse;
import inu.lecture.software_design.domain.studentrecord.service.StudentRecordService;
import inu.lecture.software_design.global.exception.CustomException;
import inu.lecture.software_design.global.exception.ErrorCode;
import inu.lecture.software_design.global.response.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 학생 본인 데이터 조회 컨트롤러 (STUDENT 역할만 접근 가능)
 */
@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentDashboardController {

    private final StudentRepository studentRepository;
    private final GradeService gradeService;
    private final FeedbackService feedbackService;
    private final StudentRecordService studentRecordService;

    /**
     * 학생 본인 성적 조회
     * GET /api/v1/student/my-grades
     */
    @GetMapping("/my-grades")
    public ResponseEntity<ApiResponse<List<GradeResponse>>> getMyGrades(
            @AuthenticationPrincipal UserDetails userDetails) {
        Student student = studentRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));
        List<GradeResponse> grades = gradeService.getGradesByStudent(student.getId());
        return ResponseEntity.ok(ApiResponse.success(grades));
    }

    /**
     * 학생 본인 피드백 조회 (published만)
     * GET /api/v1/student/my-feedbacks
     */
    @GetMapping("/my-feedbacks")
    public ResponseEntity<ApiResponse<List<FeedbackResponse>>> getMyFeedbacks(
            @AuthenticationPrincipal UserDetails userDetails) {
        Student student = studentRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));
        List<FeedbackResponse> feedbacks = feedbackService.getFeedbacks(student.getId(), null, null, null, null);
        return ResponseEntity.ok(ApiResponse.success(feedbacks));
    }

    /**
     * 학생 본인 학생부(출결) 조회
     * GET /api/v1/student/my-records
     */
    @GetMapping("/my-records")
    public ResponseEntity<ApiResponse<List<StudentRecordResponse>>> getMyRecords(
            @AuthenticationPrincipal UserDetails userDetails) {
        Student student = studentRepository.findByUsername(userDetails.getUsername())
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));
        List<StudentRecordResponse> records = studentRecordService.getStudentRecords(student.getId());
        return ResponseEntity.ok(ApiResponse.success(records));
    }
}
