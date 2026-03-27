package inu.lecture.software_design.domain.user.dto.response;

import inu.lecture.software_design.domain.user.entity.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyInfoResponse {

    private Long id;
    private String username;
    private String name;
    private String email;
    private UserRole role;
    private String phone;

    // 교사 전용
    private String department;

    // 학생 전용
    private Integer grade;
    private Integer classNum;
    private Integer studentNumber;
    private String address;

    // 학부모 전용 (연동된 자녀 이름)
    private String studentName;
    private Integer studentGrade;
    private Integer studentClassNum;
}
