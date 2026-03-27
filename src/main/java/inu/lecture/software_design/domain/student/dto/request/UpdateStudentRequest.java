package inu.lecture.software_design.domain.student.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

/**
 * CEW-28: 교사가 학생 이름/학년/반/번호 등 기본 정보를 수정
 */
@Getter
public class UpdateStudentRequest {

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotNull(message = "학년을 입력해주세요.")
    @Min(value = 1, message = "학년은 1 이상이어야 합니다.")
    @Max(value = 6, message = "학년은 6 이하여야 합니다.")
    private Integer grade;

    @NotNull(message = "반을 입력해주세요.")
    @Min(value = 1, message = "반은 1 이상이어야 합니다.")
    private Integer classNum;

    @NotNull(message = "번호를 입력해주세요.")
    @Min(value = 1, message = "번호는 1 이상이어야 합니다.")
    private Integer studentNumber;

    @Size(max = 20, message = "연락처는 20자 이하여야 합니다.")
    private String phone;

    @Size(max = 200, message = "주소는 200자 이하여야 합니다.")
    private String address;
}
