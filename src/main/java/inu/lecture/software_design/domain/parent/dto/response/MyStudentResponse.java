package inu.lecture.software_design.domain.parent.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class MyStudentResponse {

    private Long studentId;
    private String name;
    private String email;
    private Integer grade;
    private Integer classNum;
    private Integer studentNumber;
    private String phone;
    private String address;
}
