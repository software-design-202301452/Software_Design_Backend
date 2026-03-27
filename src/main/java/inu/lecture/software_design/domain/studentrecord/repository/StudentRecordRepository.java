package inu.lecture.software_design.domain.studentrecord.repository;

import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.studentrecord.entity.StudentRecord;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StudentRecordRepository extends JpaRepository<StudentRecord, Long> {

    /** 특정 학생의 모든 학생부 조회 (CEW-31) */
    List<StudentRecord> findByStudentOrderByYearDescSemesterDesc(Student student);

    /** 특정 학생의 특정 학년도/학기 학생부 중복 확인 */
    boolean existsByStudentAndYearAndSemester(Student student, Integer year, Integer semester);
}
