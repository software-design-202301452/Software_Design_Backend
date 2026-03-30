package inu.lecture.software_design.domain.feedback.service;

import inu.lecture.software_design.domain.feedback.dto.request.CreateFeedbackRequest;
import inu.lecture.software_design.domain.feedback.dto.request.UpdateFeedbackRequest;
import inu.lecture.software_design.domain.feedback.dto.response.FeedbackResponse;
import inu.lecture.software_design.domain.feedback.entity.Feedback;
import inu.lecture.software_design.domain.feedback.repository.FeedbackRepository;
import inu.lecture.software_design.domain.notification.service.NotificationService;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.student.repository.StudentRepository;
import inu.lecture.software_design.domain.teacher.entity.Teacher;
import inu.lecture.software_design.domain.teacher.repository.TeacherRepository;
import inu.lecture.software_design.global.exception.CustomException;
import inu.lecture.software_design.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class FeedbackService {

    private final FeedbackRepository feedbackRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;
    private final NotificationService notificationService;

    /**
     * CEW-39: 교사가 학생 피드백 작성
     * CEW-42: FeedbackType 지정
     */
    @Transactional
    public FeedbackResponse createFeedback(String teacherUsername, CreateFeedbackRequest request) {
        Teacher teacher = teacherRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.TEACHER_NOT_FOUND));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        Feedback feedback = feedbackRepository.save(
                Feedback.of(student, teacher, request.getFeedbackType(), request.getContent())
        );

        log.info("피드백 등록 - teacherId: {}, studentId: {}, type: {}",
                teacher.getId(), student.getId(), request.getFeedbackType());

        // CEW-58: 피드백 등록 시 학생 알림 생성
        notificationService.notifyFeedbackCreated(student, feedback);

        return toResponse(feedback);
    }

    /**
     * CEW-40: 피드백 수정
     */
    @Transactional
    public FeedbackResponse updateFeedback(Long feedbackId, UpdateFeedbackRequest request) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEEDBACK_NOT_FOUND));

        feedback.update(request.getFeedbackType(), request.getContent());
        log.info("피드백 수정 - feedbackId: {}", feedbackId);

        return toResponse(feedback);
    }

    /**
     * CEW-41: 피드백 삭제
     */
    @Transactional
    public void deleteFeedback(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEEDBACK_NOT_FOUND));
        feedbackRepository.delete(feedback);
        log.info("피드백 삭제 - feedbackId: {}", feedbackId);
    }

    /**
     * CEW-43: 피드백 공개 설정
     */
    @Transactional
    public FeedbackResponse publishFeedback(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEEDBACK_NOT_FOUND));
        feedback.publish();
        log.info("피드백 공개 - feedbackId: {}", feedbackId);

        // CEW-60: 피드백 공개 시 학생 + 학부모 알림
        notificationService.notifyFeedbackPublished(feedback.getStudent(), feedback);

        return toResponse(feedback);
    }

    /**
     * CEW-43: 피드백 비공개 설정
     */
    @Transactional
    public FeedbackResponse unpublishFeedback(Long feedbackId) {
        Feedback feedback = feedbackRepository.findById(feedbackId)
                .orElseThrow(() -> new CustomException(ErrorCode.FEEDBACK_NOT_FOUND));
        feedback.unpublish();
        log.info("피드백 비공개 - feedbackId: {}", feedbackId);
        return toResponse(feedback);
    }

    /**
     * CEW-44: 피드백 필터 조회 (studentId, teacherId, from, to 선택)
     */
    @Transactional(readOnly = true)
    public List<FeedbackResponse> getFeedbacks(Long studentId, Long teacherId, LocalDate from, LocalDate to) {
        LocalDateTime fromDt = from != null ? from.atStartOfDay() : null;
        LocalDateTime toDt = to != null ? to.plusDays(1).atStartOfDay() : null;

        return feedbackRepository.findByFilter(studentId, teacherId, fromDt, toDt)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private FeedbackResponse toResponse(Feedback f) {
        return FeedbackResponse.builder()
                .id(f.getId())
                .studentId(f.getStudent().getId())
                .studentName(f.getStudent().getName())
                .teacherId(f.getTeacher().getId())
                .teacherName(f.getTeacher().getName())
                .feedbackType(f.getFeedbackType())
                .content(f.getContent())
                .published(f.isPublished())
                .createdAt(f.getCreatedAt())
                .updatedAt(f.getUpdatedAt())
                .build();
    }
}
