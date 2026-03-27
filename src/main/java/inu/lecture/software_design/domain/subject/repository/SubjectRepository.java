package inu.lecture.software_design.domain.subject.repository;

import inu.lecture.software_design.domain.subject.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    Optional<Subject> findByName(String name);
    boolean existsByName(String name);
}
