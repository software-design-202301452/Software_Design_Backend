package inu.lecture.software_design.domain.parent.repository;

import inu.lecture.software_design.domain.parent.entity.Parent;
import inu.lecture.software_design.domain.student.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ParentRepository extends JpaRepository<Parent, Long> {
    Optional<Parent> findByUsername(String username);
    List<Parent> findByStudent(Student student);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
