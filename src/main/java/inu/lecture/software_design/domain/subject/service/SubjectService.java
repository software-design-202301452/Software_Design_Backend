package inu.lecture.software_design.domain.subject.service;

import inu.lecture.software_design.domain.subject.dto.request.CreateSubjectRequest;
import inu.lecture.software_design.domain.subject.dto.response.SubjectResponse;
import inu.lecture.software_design.domain.subject.entity.Subject;
import inu.lecture.software_design.domain.subject.repository.SubjectRepository;
import inu.lecture.software_design.global.exception.CustomException;
import inu.lecture.software_design.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubjectService {

    private final SubjectRepository subjectRepository;

    /** 과목 생성 */
    @Transactional
    public SubjectResponse createSubject(CreateSubjectRequest request) {
        if (subjectRepository.existsByName(request.getName())) {
            throw new CustomException(ErrorCode.SUBJECT_ALREADY_EXISTS);
        }
        Subject subject = subjectRepository.save(
                Subject.of(request.getName(), request.getDescription())
        );
        log.info("과목 생성 - name: {}", request.getName());
        return toResponse(subject);
    }

    /** 전체 과목 목록 조회 */
    @Transactional(readOnly = true)
    public List<SubjectResponse> getAllSubjects() {
        return subjectRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private SubjectResponse toResponse(Subject s) {
        return SubjectResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .description(s.getDescription())
                .build();
    }
}
