package inu.lecture.software_design.domain.counseling.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Builder
public class CounselingResponse {
    private Long id;
    private Long studentId;
    private String studentName;
    private Long teacherId;
    private String teacherName;
    private LocalDate counselingDate;
    private String content;
    private String nextPlan;
    private LocalDate nextCounselingDate;
    private boolean shared;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
