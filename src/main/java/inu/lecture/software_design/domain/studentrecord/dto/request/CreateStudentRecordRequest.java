package inu.lecture.software_design.domain.studentrecord.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDate;

/**
 * CEW-29: 출결/특기사항 등 학생부 항목 기록
 */
@Getter
public class CreateStudentRecordRequest {

    @NotNull(message = "학생 ID를 입력해주세요.")
    private Long studentId;

    @NotNull(message = "학년도를 입력해주세요.")
    private Integer year;

    @NotNull(message = "학기를 입력해주세요.")
    @Min(value = 1, message = "학기는 1 또는 2여야 합니다.")
    @Max(value = 2, message = "학기는 1 또는 2여야 합니다.")
    private Integer semester;

    @NotNull(message = "기록일을 입력해주세요.")
    private LocalDate recordDate;

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
