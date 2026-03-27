package inu.lecture.software_design.domain.feedback.dto.request;

import inu.lecture.software_design.domain.feedback.entity.FeedbackType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class CreateFeedbackRequest {

    @NotNull(message = "학생 ID는 필수입니다.")
    private Long studentId;

    @NotNull(message = "피드백 유형은 필수입니다.")
    private FeedbackType feedbackType;

    @NotBlank(message = "피드백 내용은 필수입니다.")
    private String content;
}
