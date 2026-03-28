package inu.lecture.software_design.domain.counseling.repository;

import inu.lecture.software_design.domain.counseling.entity.Counseling;
import inu.lecture.software_design.domain.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface CounselingRepository extends JpaRepository<Counseling, Long> {

    List<Counseling> findByStudentOrderByCounselingDateDesc(Student student);

    List<Counseling> findBySharedTrueOrderByCounselingDateDesc();

    /** CEW-50/51: 공유 상담 키워드/날짜/학생/교사 기준 검색 */
    @Query("SELECT c FROM Counseling c WHERE c.shared = true AND " +
           "(:keyword IS NULL OR c.content LIKE %:keyword% OR c.nextPlan LIKE %:keyword%) AND " +
           "(:from IS NULL OR c.counselingDate >= :from) AND " +
           "(:to IS NULL OR c.counselingDate <= :to) AND " +
           "(:studentId IS NULL OR c.student.id = :studentId) AND " +
           "(:teacherId IS NULL OR c.teacher.id = :teacherId) " +
           "ORDER BY c.counselingDate DESC")
    List<Counseling> searchSharedCounselings(
            @Param("keyword") String keyword,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to,
            @Param("studentId") Long studentId,
            @Param("teacherId") Long teacherId);

    /** CEW-51/55: 특정 학생 상담 날짜 범위 필터 */
    @Query("SELECT c FROM Counseling c WHERE c.student = :student AND " +
           "(:from IS NULL OR c.counselingDate >= :from) AND " +
           "(:to IS NULL OR c.counselingDate <= :to) " +
           "ORDER BY c.counselingDate DESC")
    List<Counseling> findByStudentWithDateFilter(
            @Param("student") Student student,
            @Param("from") LocalDate from,
            @Param("to") LocalDate to);

    /** CEW-69: 최근 상담 n건 조회 */
    List<Counseling> findTop5ByOrderByCounselingDateDesc();
}
