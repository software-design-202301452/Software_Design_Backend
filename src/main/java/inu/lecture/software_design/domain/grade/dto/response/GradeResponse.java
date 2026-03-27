package inu.lecture.software_design.domain.grade.dto.response;

import inu.lecture.software_design.domain.grade.entity.GradeLevel;
import lombok.Builder;
import lombok.Getter;

/**
 * CEW-32,33,37,38: 성적 응답 DTO
 */
@Getter
@Builder
public class GradeResponse {

    private Long id;

    // 학생 정보
    private Long studentId;
    private String studentName;
    private Integer grade;
    private Integer classNum;
    private Integer studentNumber;

    // 과목 정보
    private Long subjectId;
    private String subjectName;

    // 교사 정보
    private Long teacherId;
    private String teacherName;

    // 성적 정보
    private Integer year;
    private Integer semester;
    private Double score;
    private Double totalScore;

    /** CEW-35: 자동 계산된 평균(백분율) */
    private Double average;

    /** CEW-36: 자동 산출된 등급 (A/B/C/D/E) */
    private GradeLevel gradeLevel;

    private String note;
}
