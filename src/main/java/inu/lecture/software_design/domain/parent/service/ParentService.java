package inu.lecture.software_design.domain.parent.service;

import inu.lecture.software_design.domain.feedback.dto.response.FeedbackResponse;
import inu.lecture.software_design.domain.feedback.service.FeedbackService;
import inu.lecture.software_design.domain.grade.dto.response.GradeResponse;
import inu.lecture.software_design.domain.grade.service.GradeService;
import inu.lecture.software_design.domain.parent.dto.response.MyStudentResponse;
import inu.lecture.software_design.domain.parent.entity.Parent;
import inu.lecture.software_design.domain.parent.repository.ParentRepository;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.global.exception.CustomException;
import inu.lecture.software_design.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ParentService {

    private final ParentRepository parentRepository;
    private final GradeService gradeService;
    private final FeedbackService feedbackService;

    /**
     * 학부모 본인과 연동된 자녀(학생) 정보 조회
     */
    @Transactional(readOnly = true)
    public MyStudentResponse getMyStudent(String username) {
        Student student = getLinkedStudent(username);
        return MyStudentResponse.builder()
                .studentId(student.getId())
                .name(student.getName())
                .email(student.getEmail())
                .grade(student.getGrade())
                .classNum(student.getClassNum())
                .studentNumber(student.getStudentNumber())
                .phone(student.getPhone())
                .address(student.getAddress())
                .build();
    }

    /**
     * 학부모가 연동된 자녀의 성적 조회
     */
    @Transactional(readOnly = true)
    public List<GradeResponse> getStudentGrades(String username) {
        Student student = getLinkedStudent(username);
        return gradeService.getGradesByStudent(student.getId());
    }

    /**
     * 학부모가 연동된 자녀의 피드백 조회 (published만)
     */
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getStudentFeedbacks(String username) {
        Student student = getLinkedStudent(username);
        return feedbackService.getFeedbacks(student.getId(), null, null, null, null)
                .stream()
                .filter(FeedbackResponse::isPublished)
                .toList();
    }

    private Student getLinkedStudent(String username) {
        Parent parent = parentRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.PARENT_NOT_FOUND));
        return parent.getStudent();
    }
}
