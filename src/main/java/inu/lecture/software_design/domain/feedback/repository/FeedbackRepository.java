package inu.lecture.software_design.domain.feedback.repository;

import inu.lecture.software_design.domain.feedback.entity.Feedback;
import inu.lecture.software_design.domain.feedback.entity.FeedbackType;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.teacher.entity.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("SELECT f FROM Feedback f WHERE " +
            "(:studentId IS NULL OR f.student.id = :studentId) AND " +
            "(:teacherId IS NULL OR f.teacher.id = :teacherId) AND " +
            "(:feedbackType IS NULL OR f.feedbackType = :feedbackType) AND " +
            "(:from IS NULL OR f.createdAt >= :from) AND " +
            "(:to IS NULL OR f.createdAt <= :to) " +
            "ORDER BY f.createdAt DESC")
    List<Feedback> findByFilter(
            @Param("studentId") Long studentId,
            @Param("teacherId") Long teacherId,
            @Param("feedbackType") FeedbackType feedbackType,
            @Param("from") java.time.LocalDateTime from,
            @Param("to") java.time.LocalDateTime to
    );

    List<Feedback> findByStudentAndPublishedTrue(Student student);

    /** CEW-69: 최근 피드백 n건 조회 */
    List<Feedback> findTop5ByOrderByCreatedAtDesc();

    /** CEW-113/114: 분석용 - 학생별 피드백 수 */
    long countByStudentId(Long studentId);
}
