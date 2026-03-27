package inu.lecture.software_design.domain.studentrecord.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

/**
 * CEW-29,30,31: 학생부 응답 DTO
 */
@Getter
@Builder
public class StudentRecordResponse {

    private Long id;

    // 학생 정보
    private Long studentId;
    private String studentName;
    private Integer grade;
    private Integer classNum;
    private Integer studentNumber;

    // 교사 정보
    private Long teacherId;
    private String teacherName;

    // 학생부 내용
    private Integer year;
    private Integer semester;
    private LocalDate recordDate;
    private Integer attendanceDays;
    private Integer absenceDays;
    private Integer lateDays;
    private Integer earlyLeaveDays;
    private String specialNote;
    private Integer volunteerHours;
}
