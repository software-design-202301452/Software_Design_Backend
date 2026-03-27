package inu.lecture.software_design.domain.grade.repository;

import inu.lecture.software_design.domain.grade.entity.Grade;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.subject.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {

    boolean existsByStudentAndSubjectAndYearAndSemester(
            Student student, Subject subject, Integer year, Integer semester);

    /** CEW-37: 학생별 전체 성적 조회 (연도 내림차순, 학기 오름차순) */
    List<Grade> findByStudentOrderByYearDescSemesterAsc(Student student);

    /** CEW-38: 과목/연도/학기 기준 필터 조회 (null 이면 전체) */
    @Query("SELECT g FROM Grade g WHERE g.student = :student " +
           "AND (:subjectId IS NULL OR g.subject.id = :subjectId) " +
           "AND (:year IS NULL OR g.year = :year) " +
           "AND (:semester IS NULL OR g.semester = :semester) " +
           "ORDER BY g.year DESC, g.semester ASC")
    List<Grade> findByStudentWithFilter(
            @Param("student") Student student,
            @Param("subjectId") Long subjectId,
            @Param("year") Integer year,
            @Param("semester") Integer semester);
}
