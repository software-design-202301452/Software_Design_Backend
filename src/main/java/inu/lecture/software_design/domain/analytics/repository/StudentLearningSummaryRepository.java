package inu.lecture.software_design.domain.analytics.repository;

import inu.lecture.software_design.domain.analytics.entity.StudentLearningSummary;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface StudentLearningSummaryRepository extends JpaRepository<StudentLearningSummary, Long> {

    Optional<StudentLearningSummary> findByStudentId(Long studentId);

    /** 학년/반 기준 평균 조회 */
    @Query("SELECT s.grade, s.classNum, AVG(s.overallAverage) FROM StudentLearningSummary s GROUP BY s.grade, s.classNum ORDER BY s.grade, s.classNum")
    List<Object[]> findClassAverages();

    /** 전체 평균 상위 학생 */
    List<StudentLearningSummary> findTop10ByOrderByOverallAverageDesc();
}
