package inu.lecture.software_design.domain.admin.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
public class AdminCreateStudentRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 4, max = 50, message = "아이디는 4~50자 이내여야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, message = "비밀번호는 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

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
