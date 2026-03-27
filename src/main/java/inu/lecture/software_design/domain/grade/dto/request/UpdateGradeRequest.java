package inu.lecture.software_design.domain.grade.dto.request;

import jakarta.validation.constraints.*;
import lombok.Getter;

/**
 * CEW-33: 이미 등록된 성적 수정
 * - average, gradeLevel 은 수정된 score/totalScore 기반으로 자동 재계산됨 (CEW-35, CEW-36)
 */
@Getter
public class UpdateGradeRequest {

    @NotNull(message = "점수를 입력해주세요.")
    @DecimalMin(value = "0.0", message = "점수는 0 이상이어야 합니다.")
    private Double score;

    @NotNull(message = "총점을 입력해주세요.")
    @DecimalMin(value = "1.0", message = "총점은 1 이상이어야 합니다.")
    private Double totalScore;

    @Size(max = 500, message = "비고는 500자 이하여야 합니다.")
    private String note;
}
