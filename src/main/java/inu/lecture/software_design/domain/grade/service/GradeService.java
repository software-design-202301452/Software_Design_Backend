package inu.lecture.software_design.domain.grade.service;

import inu.lecture.software_design.domain.grade.dto.request.CreateGradeRequest;
import inu.lecture.software_design.domain.grade.dto.request.UpdateGradeRequest;
import inu.lecture.software_design.domain.grade.dto.response.GradeResponse;
import inu.lecture.software_design.domain.grade.entity.Grade;
import inu.lecture.software_design.domain.grade.repository.GradeRepository;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.student.repository.StudentRepository;
import inu.lecture.software_design.domain.subject.entity.Subject;
import inu.lecture.software_design.domain.subject.repository.SubjectRepository;
import inu.lecture.software_design.domain.teacher.entity.Teacher;
import inu.lecture.software_design.domain.teacher.repository.TeacherRepository;
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
public class GradeService {

    private final GradeRepository gradeRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;

    /**
     * CEW-32: 교사가 학생 성적 입력
     * CEW-35: score/totalScore → average = (score/totalScore)*100 자동 계산
     * CEW-36: average → gradeLevel 자동 산출 (Grade 엔티티 내부에서 처리)
     */
    @Transactional
    public GradeResponse createGrade(String teacherUsername, CreateGradeRequest request) {
        Teacher teacher = teacherRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.TEACHER_NOT_FOUND));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        Subject subject = subjectRepository.findById(request.getSubjectId())
                .orElseThrow(() -> new CustomException(ErrorCode.SUBJECT_NOT_FOUND));

        if (gradeRepository.existsByStudentAndSubjectAndYearAndSemester(
                student, subject, request.getYear(), request.getSemester())) {
            throw new CustomException(ErrorCode.GRADE_ALREADY_EXISTS);
        }

        // CEW-35: 평균(백분율) 자동 계산
        double average = (request.getScore() / request.getTotalScore()) * 100.0;

        // CEW-36: gradeLevel 은 Grade 엔티티 생성자 내부에서 GradeLevel.from(score/totalScore*100) 으로 자동 산출
        Grade grade = gradeRepository.save(
                Grade.of(student, subject, teacher,
                        request.getYear(), request.getSemester(),
                        request.getScore(), request.getTotalScore(), average, request.getNote())
        );

        log.info("성적 등록 - studentId: {}, subjectId: {}, year: {}, semester: {}, grade: {}",
                student.getId(), subject.getId(), request.getYear(), request.getSemester(), grade.getGradeLevel());

        return toResponse(grade);
    }

    /**
     * CEW-33: 이미 등록된 성적 수정
     * CEW-35, CEW-36: 수정 시에도 평균/등급 재자동 계산
     */
    @Transactional
    public GradeResponse updateGrade(Long gradeId, UpdateGradeRequest request) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new CustomException(ErrorCode.GRADE_NOT_FOUND));

        // CEW-35: 평균 재계산
        double average = (request.getScore() / request.getTotalScore()) * 100.0;

        // CEW-36: gradeLevel 재자동 산출 (updateScore 내부에서 처리)
        grade.updateScore(request.getScore(), request.getTotalScore(), average, request.getNote());

        log.info("성적 수정 - gradeId: {}, 새 등급: {}", gradeId, grade.getGradeLevel());

        return toResponse(grade);
    }

    /**
     * CEW-34: 잘못 입력된 성적 삭제
     */
    @Transactional
    public void deleteGrade(Long gradeId) {
        Grade grade = gradeRepository.findById(gradeId)
                .orElseThrow(() -> new CustomException(ErrorCode.GRADE_NOT_FOUND));
        gradeRepository.delete(grade);
        log.info("성적 삭제 - gradeId: {}", gradeId);
    }

    /**
     * CEW-37: 학생별 전체 성적 조회
     */
    @Transactional(readOnly = true)
    public List<GradeResponse> getGradesByStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        return gradeRepository.findByStudentOrderByYearDescSemesterAsc(student)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * CEW-38: 과목별/학기별 성적 필터 조회 (파라미터 null 이면 전체)
     */
    @Transactional(readOnly = true)
    public List<GradeResponse> getGradesByFilter(Long studentId, Long subjectId, Integer year, Integer semester) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        return gradeRepository.findByStudentWithFilter(student, subjectId, year, semester)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private GradeResponse toResponse(Grade g) {
        return GradeResponse.builder()
                .id(g.getId())
                .studentId(g.getStudent().getId())
                .studentName(g.getStudent().getName())
                .grade(g.getStudent().getGrade())
                .classNum(g.getStudent().getClassNum())
                .studentNumber(g.getStudent().getStudentNumber())
                .subjectId(g.getSubject().getId())
                .subjectName(g.getSubject().getName())
                .teacherId(g.getTeacher().getId())
                .teacherName(g.getTeacher().getName())
                .year(g.getYear())
                .semester(g.getSemester())
                .score(g.getScore())
                .totalScore(g.getTotalScore())
                .average(g.getAverage())
                .gradeLevel(g.getGradeLevel())
                .note(g.getNote())
                .build();
    }
}
