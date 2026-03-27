package inu.lecture.software_design.domain.student.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * CEW-27: 특정 학생의 기본 정보와 학생부 정보를 함께 반환하는 응답 DTO
 */
@Getter
@Builder
public class StudentDetailResponse {

    // 학생 기본 정보
    private Long id;
    private String name;
    private String username;
    private String email;
    private Integer grade;
    private Integer classNum;
    private Integer studentNumber;
    private String phone;
    private String address;

    // 학생부 목록
    private List<StudentRecordSummary> studentRecords;

    @Getter
    @Builder
    public static class StudentRecordSummary {
        private Long id;
        private Integer year;
        private Integer semester;
        private LocalDate recordDate;
        private Integer attendanceDays;
        private Integer absenceDays;
        private Integer lateDays;
        private Integer earlyLeaveDays;
        private Integer volunteerHours;
        private String specialNote;
        private String teacherName;
    }
}
