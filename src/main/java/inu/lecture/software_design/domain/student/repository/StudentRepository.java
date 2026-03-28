package inu.lecture.software_design.domain.student.repository;

import inu.lecture.software_design.domain.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByUsername(String username);
    Optional<Student> findByLinkCode(String linkCode);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByGradeAndClassNumAndStudentNumber(Integer grade, Integer classNum, Integer studentNumber);

    /** 학년/반/번호 기준으로 학생 목록 조회 (CEW-26) - 파라미터가 null이면 전체 조회 */
    @Query("SELECT s FROM Student s WHERE " +
           "(:grade IS NULL OR s.grade = :grade) AND " +
           "(:classNum IS NULL OR s.classNum = :classNum) AND " +
           "(:studentNumber IS NULL OR s.studentNumber = :studentNumber) " +
           "ORDER BY s.grade ASC, s.classNum ASC, s.studentNumber ASC")
    List<Student> findByFilter(@Param("grade") Integer grade,
                               @Param("classNum") Integer classNum,
                               @Param("studentNumber") Integer studentNumber);

    /** CEW-52: 이름(부분일치)/학년/반/번호 기준 학생 검색 */
    @Query("SELECT s FROM Student s WHERE " +
           "(:name IS NULL OR s.name LIKE %:name%) AND " +
           "(:grade IS NULL OR s.grade = :grade) AND " +
           "(:classNum IS NULL OR s.classNum = :classNum) AND " +
           "(:studentNumber IS NULL OR s.studentNumber = :studentNumber) " +
           "ORDER BY s.grade ASC, s.classNum ASC, s.studentNumber ASC")
    List<Student> findByFilterWithName(@Param("name") String name,
                                       @Param("grade") Integer grade,
                                       @Param("classNum") Integer classNum,
                                       @Param("studentNumber") Integer studentNumber);
}
