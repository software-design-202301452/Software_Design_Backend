package inu.lecture.software_design.domain.counseling.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class UpdateCounselingRequest {

    @NotBlank(message = "상담 내용은 필수입니다.")
    private String content;

    private String nextPlan;

    private LocalDate nextCounselingDate;
}
