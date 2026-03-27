package inu.lecture.software_design.domain.counseling.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class CreateCounselingRequest {

    @NotNull(message = "학생 ID는 필수입니다.")
    private Long studentId;

    @NotNull(message = "상담 날짜는 필수입니다.")
    private LocalDate counselingDate;

    @NotBlank(message = "상담 내용은 필수입니다.")
    private String content;

    private String nextPlan;

    private LocalDate nextCounselingDate;
}
