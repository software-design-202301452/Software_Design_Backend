package inu.lecture.software_design.domain.subject.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class CreateSubjectRequest {

    @NotBlank(message = "과목명을 입력해주세요.")
    @Size(max = 100, message = "과목명은 100자 이하여야 합니다.")
    private String name;

    @Size(max = 300, message = "설명은 300자 이하여야 합니다.")
    private String description;
}
