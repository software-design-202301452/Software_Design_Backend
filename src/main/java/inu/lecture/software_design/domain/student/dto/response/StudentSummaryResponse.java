package inu.lecture.software_design.domain.student.dto.response;

import lombok.Builder;
import lombok.Getter;

/**
 * CEW-26: 학년/반/번호 기준 학생 목록 응답 DTO
 */
@Getter
@Builder
public class StudentSummaryResponse {

    private Long id;
    private String name;
    private String username;
    private String email;
    private Integer grade;
    private Integer classNum;
    private Integer studentNumber;
    private String phone;
}
