package inu.lecture.software_design.domain.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignupStudentRequest {

    @NotBlank(message = "아이디를 입력해주세요.")
    @Size(min = 4, max = 50, message = "아이디는 4~50자 사이여야 합니다.")
    private String username;

    @NotBlank(message = "비밀번호를 입력해주세요.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "이름을 입력해주세요.")
    private String name;

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "올바른 이메일 형식이 아닙니다.")
    private String email;

    @NotNull(message = "학년을 입력해주세요.")
    private Integer grade;

    @NotNull(message = "반을 입력해주세요.")
    private Integer classNum;

    @NotNull(message = "번호를 입력해주세요.")
    private Integer studentNumber;

    private String phone;
    private String address;
}
