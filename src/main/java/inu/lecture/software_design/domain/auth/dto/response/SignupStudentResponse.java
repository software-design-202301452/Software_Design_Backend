package inu.lecture.software_design.domain.auth.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class SignupStudentResponse {
    private final Long studentId;
    private final String name;
    private final Integer grade;
    private final Integer classNum;
    private final Integer studentNumber;
    /**
     * 학부모 연동용 고유 코드
     * 이 코드를 학부모에게 전달하면, 학부모가 자녀 계정과 연동하여 가입 가능
     */
    private final String linkCode;
}
