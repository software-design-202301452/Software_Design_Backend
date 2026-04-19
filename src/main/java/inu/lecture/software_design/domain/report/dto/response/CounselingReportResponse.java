package inu.lecture.software_design.domain.report.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

/**
 * CEW-64: 상담 기록 보고서 DTO
 */
@Getter
@Builder
public class CounselingReportResponse {

    private Long studentId;
    private String studentName;
    private LocalDate generatedAt;
    private Integer totalCount;

    private List<CounselingItemDto> counselings;

    @Getter
    @Builder
    public static class CounselingItemDto {
        private LocalDate counselingDate;
        private String teacherName;
        private String content;
        private String nextPlan;
        private LocalDate nextCounselingDate;
    }
}
