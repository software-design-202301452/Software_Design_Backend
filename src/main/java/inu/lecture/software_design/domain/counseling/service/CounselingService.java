package inu.lecture.software_design.domain.counseling.service;

import inu.lecture.software_design.domain.counseling.dto.request.CreateCounselingRequest;
import inu.lecture.software_design.domain.counseling.dto.request.UpdateCounselingRequest;
import inu.lecture.software_design.domain.counseling.dto.response.CounselingResponse;
import inu.lecture.software_design.domain.counseling.entity.Counseling;
import inu.lecture.software_design.domain.counseling.repository.CounselingRepository;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.student.repository.StudentRepository;
import inu.lecture.software_design.domain.teacher.entity.Teacher;
import inu.lecture.software_design.domain.teacher.repository.TeacherRepository;
import inu.lecture.software_design.global.exception.CustomException;
import inu.lecture.software_design.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CounselingService {

    private final CounselingRepository counselingRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    /**
     * CEW-45: 상담 날짜/내용/다음 계획 등록
     */
    @Transactional
    public CounselingResponse createCounseling(String teacherUsername, CreateCounselingRequest request) {
        Teacher teacher = teacherRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.TEACHER_NOT_FOUND));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        Counseling counseling = counselingRepository.save(
                Counseling.of(student, teacher,
                        request.getCounselingDate(),
                        request.getContent(),
                        request.getNextPlan(),
                        request.getNextCounselingDate())
        );

        log.info("상담 등록 - teacherId: {}, studentId: {}, date: {}",
                teacher.getId(), student.getId(), request.getCounselingDate());

        return toResponse(counseling);
    }

    /**
     * CEW-46: 상담 수정
     */
    @Transactional
    public CounselingResponse updateCounseling(Long counselingId, UpdateCounselingRequest request) {
        Counseling counseling = counselingRepository.findById(counselingId)
                .orElseThrow(() -> new CustomException(ErrorCode.COUNSELING_NOT_FOUND));

        counseling.update(request.getContent(), request.getNextPlan(), request.getNextCounselingDate());
        log.info("상담 수정 - counselingId: {}", counselingId);

        return toResponse(counseling);
    }

    /**
     * CEW-47: 상담 삭제
     */
    @Transactional
    public void deleteCounseling(Long counselingId) {
        Counseling counseling = counselingRepository.findById(counselingId)
                .orElseThrow(() -> new CustomException(ErrorCode.COUNSELING_NOT_FOUND));
        counselingRepository.delete(counseling);
        log.info("상담 삭제 - counselingId: {}", counselingId);
    }

    /**
     * CEW-48/55: 특정 학생 상담 이력 조회 (날짜 범위 필터 선택)
     */
    @Transactional(readOnly = true)
    public List<CounselingResponse> getCounselingsByStudent(Long studentId, LocalDate from, LocalDate to) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        return counselingRepository.findByStudentWithDateFilter(student, from, to)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * CEW-49: 공유된 상담 기록 전체 조회
     */
    @Transactional(readOnly = true)
    public List<CounselingResponse> getSharedCounselings() {
        return counselingRepository.findBySharedTrueOrderByCounselingDateDesc()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * CEW-50/51: 공유 상담 키워드/날짜/학생/교사 기준 검색
     */
    @Transactional(readOnly = true)
    public List<CounselingResponse> searchSharedCounselings(String keyword, LocalDate from, LocalDate to,
                                                             Long studentId, Long teacherId) {
        return counselingRepository.searchSharedCounselings(keyword, from, to, studentId, teacherId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * CEW-49: 상담 공유 설정
     */
    @Transactional
    public CounselingResponse shareCounseling(Long counselingId) {
        Counseling counseling = counselingRepository.findById(counselingId)
                .orElseThrow(() -> new CustomException(ErrorCode.COUNSELING_NOT_FOUND));
        counseling.share();
        log.info("상담 공유 - counselingId: {}", counselingId);
        return toResponse(counseling);
    }

    /**
     * CEW-49: 상담 공유 해제
     */
    @Transactional
    public CounselingResponse unshareCounseling(Long counselingId) {
        Counseling counseling = counselingRepository.findById(counselingId)
                .orElseThrow(() -> new CustomException(ErrorCode.COUNSELING_NOT_FOUND));
        counseling.unshare();
        log.info("상담 공유 해제 - counselingId: {}", counselingId);
        return toResponse(counseling);
    }

    private CounselingResponse toResponse(Counseling c) {
        return CounselingResponse.builder()
                .id(c.getId())
                .studentId(c.getStudent().getId())
                .studentName(c.getStudent().getName())
                .teacherId(c.getTeacher().getId())
                .teacherName(c.getTeacher().getName())
                .counselingDate(c.getCounselingDate())
                .content(c.getContent())
                .nextPlan(c.getNextPlan())
                .nextCounselingDate(c.getNextCounselingDate())
                .shared(c.isShared())
                .createdAt(c.getCreatedAt())
                .updatedAt(c.getUpdatedAt())
                .build();
    }
}
