package inu.lecture.software_design.domain.user.dto.request;

import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UpdateMyInfoRequest {

    /** 교사: 담당 과목/부서 */
    private String department;

    /** 공통: 연락처 */
    @Size(max = 20, message = "연락처는 20자 이하여야 합니다.")
    private String phone;

    /** 학생: 주소 */
    @Size(max = 200, message = "주소는 200자 이하여야 합니다.")
    private String address;
}
