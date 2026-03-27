package inu.lecture.software_design.domain.counseling.repository;

import inu.lecture.software_design.domain.counseling.entity.Counseling;
import inu.lecture.software_design.domain.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CounselingRepository extends JpaRepository<Counseling, Long> {

    List<Counseling> findByStudentOrderByCounselingDateDesc(Student student);

    List<Counseling> findBySharedTrueOrderByCounselingDateDesc();
}
