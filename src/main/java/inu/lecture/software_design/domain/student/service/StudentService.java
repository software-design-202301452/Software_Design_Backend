package inu.lecture.software_design.domain.student.service;

import inu.lecture.software_design.domain.student.dto.request.RegisterStudentRequest;
import inu.lecture.software_design.domain.student.dto.request.UpdateStudentRequest;
import inu.lecture.software_design.domain.student.dto.response.StudentDetailResponse;
import inu.lecture.software_design.domain.student.dto.response.StudentDetailResponse.StudentRecordSummary;
import inu.lecture.software_design.domain.student.dto.response.StudentSummaryResponse;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.student.repository.StudentRepository;
import inu.lecture.software_design.domain.studentrecord.repository.StudentRecordRepository;
import inu.lecture.software_design.global.exception.CustomException;
import inu.lecture.software_design.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;
    private final StudentRecordRepository studentRecordRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * CEW-25: 교사가 학생 기본 정보를 등록
     */
    @Transactional
    public StudentSummaryResponse registerStudent(RegisterStudentRequest request) {
        if (studentRepository.existsByUsername(request.getUsername())) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
        if (studentRepository.existsByEmail(request.getEmail())) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
        if (studentRepository.existsByGradeAndClassNumAndStudentNumber(
                request.getGrade(), request.getClassNum(), request.getStudentNumber())) {
            throw new CustomException(ErrorCode.STUDENT_ALREADY_REGISTERED);
        }

        Student student = studentRepository.save(
                Student.of(request.getUsername(),
                        passwordEncoder.encode(request.getPassword()),
                        request.getEmail(),
                        request.getName(),
                        request.getGrade(),
                        request.getClassNum(),
                        request.getStudentNumber(),
                        request.getPhone(),
                        request.getAddress())
        );

        log.info("교사가 학생 등록 - username: {}", request.getUsername());

        return toSummary(student);
    }

    /**
     * CEW-26: 학년/반/번호 기준으로 학생 목록 조회 (null이면 전체)
     */
    @Transactional(readOnly = true)
    public List<StudentSummaryResponse> getStudents(Integer grade, Integer classNum, Integer studentNumber) {
        return studentRepository.findByFilter(grade, classNum, studentNumber)
                .stream()
                .map(this::toSummary)
                .toList();
    }

    /**
     * CEW-27: 특정 학생의 기본 정보 + 학생부 목록 조회
     */
    @Transactional(readOnly = true)
    public StudentDetailResponse getStudentDetail(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        List<StudentRecordSummary> records = studentRecordRepository
                .findByStudentOrderByYearDescSemesterDesc(student)
                .stream()
                .map(r -> StudentRecordSummary.builder()
                        .id(r.getId())
                        .year(r.getYear())
                        .semester(r.getSemester())
                        .recordDate(r.getRecordDate())
                        .attendanceDays(r.getAttendanceDays())
                        .absenceDays(r.getAbsenceDays())
                        .lateDays(r.getLateDays())
                        .earlyLeaveDays(r.getEarlyLeaveDays())
                        .volunteerHours(r.getVolunteerHours())
                        .specialNote(r.getSpecialNote())
                        .teacherName(r.getTeacher().getName())
                        .build())
                .toList();

        return StudentDetailResponse.builder()
                .id(student.getId())
                .name(student.getName())
                .username(student.getUsername())
                .email(student.getEmail())
                .grade(student.getGrade())
                .classNum(student.getClassNum())
                .studentNumber(student.getStudentNumber())
                .phone(student.getPhone())
                .address(student.getAddress())
                .studentRecords(records)
                .build();
    }

    /**
     * CEW-28: 학생 이름/학년/반/번호 등 기본 정보 수정
     */
    @Transactional
    public void updateStudent(Long studentId, UpdateStudentRequest request) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        // 다른 학생과의 학년/반/번호 중복 확인 (본인 제외)
        studentRepository.findByFilter(request.getGrade(), request.getClassNum(), request.getStudentNumber())
                .stream()
                .filter(s -> !s.getId().equals(studentId))
                .findAny()
                .ifPresent(s -> { throw new CustomException(ErrorCode.STUDENT_ALREADY_REGISTERED); });

        student.updateBasicInfo(request.getName(), request.getGrade(),
                request.getClassNum(), request.getStudentNumber());
        student.updateContact(request.getPhone(), request.getAddress());

        log.info("학생 기본정보 수정 - studentId: {}", studentId);
    }

    private StudentSummaryResponse toSummary(Student s) {
        return StudentSummaryResponse.builder()
                .id(s.getId())
                .name(s.getName())
                .username(s.getUsername())
                .email(s.getEmail())
                .grade(s.getGrade())
                .classNum(s.getClassNum())
                .studentNumber(s.getStudentNumber())
                .phone(s.getPhone())
                .build();
    }
}
