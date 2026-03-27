package inu.lecture.software_design.domain.grade.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

/**
 * CEW-32: 교사가 학생 성적을 입력
 * - average, gradeLevel 은 score/totalScore 기반으로 자동 계산됨 (CEW-35, CEW-36)
 */
@Getter
public class CreateGradeRequest {

    @NotNull(message = "학생 ID를 입력해주세요.")
    private Long studentId;

    @NotNull(message = "과목 ID를 입력해주세요.")
    private Long subjectId;

    @NotNull(message = "학년도를 입력해주세요.")
    private Integer year;

    @NotNull(message = "학기를 입력해주세요.")
    @Min(value = 1, message = "학기는 1 또는 2여야 합니다.")
    @Max(value = 2, message = "학기는 1 또는 2여야 합니다.")
    private Integer semester;

    @NotNull(message = "점수를 입력해주세요.")
    @DecimalMin(value = "0.0", message = "점수는 0 이상이어야 합니다.")
    private Double score;

    @NotNull(message = "총점을 입력해주세요.")
    @DecimalMin(value = "1.0", message = "총점은 1 이상이어야 합니다.")
    private Double totalScore;

    @Size(max = 500, message = "비고는 500자 이하여야 합니다.")
    private String note;
}
