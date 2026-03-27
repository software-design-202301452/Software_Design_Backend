package inu.lecture.software_design.domain.studentrecord.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Getter;

/**
 * CEW-30: 기존 학생부 항목 수정
 */
@Getter
public class UpdateStudentRecordRequest {

    @Min(value = 0, message = "출석일수는 0 이상이어야 합니다.")
    private Integer attendanceDays;

    @Min(value = 0, message = "결석일수는 0 이상이어야 합니다.")
    private Integer absenceDays;

    @Min(value = 0, message = "지각횟수는 0 이상이어야 합니다.")
    private Integer lateDays;

    @Min(value = 0, message = "조퇴횟수는 0 이상이어야 합니다.")
    private Integer earlyLeaveDays;

    private String specialNote;

    @Min(value = 0, message = "봉사활동 시간은 0 이상이어야 합니다.")
    private Integer volunteerHours;
}
