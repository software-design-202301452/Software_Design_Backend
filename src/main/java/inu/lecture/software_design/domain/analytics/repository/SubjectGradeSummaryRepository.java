package inu.lecture.software_design.domain.analytics.repository;

import inu.lecture.software_design.domain.analytics.entity.SubjectGradeSummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SubjectGradeSummaryRepository extends JpaRepository<SubjectGradeSummary, Long> {

    Optional<SubjectGradeSummary> findBySubjectId(Long subjectId);

    List<SubjectGradeSummary> findAllByOrderByAvgScoreDesc();
}
