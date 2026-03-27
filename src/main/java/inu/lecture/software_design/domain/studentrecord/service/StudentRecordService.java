package inu.lecture.software_design.domain.studentrecord.service;

import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.student.repository.StudentRepository;
import inu.lecture.software_design.domain.studentrecord.dto.request.CreateStudentRecordRequest;
import inu.lecture.software_design.domain.studentrecord.dto.request.UpdateStudentRecordRequest;
import inu.lecture.software_design.domain.studentrecord.dto.response.StudentRecordResponse;
import inu.lecture.software_design.domain.studentrecord.entity.StudentRecord;
import inu.lecture.software_design.domain.studentrecord.repository.StudentRecordRepository;
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
public class StudentRecordService {

    private final StudentRecordRepository studentRecordRepository;
    private final StudentRepository studentRepository;
    private final TeacherRepository teacherRepository;

    /**
     * CEW-29: 출결/특기사항 등 학생부 항목 기록
     */
    @Transactional
    public StudentRecordResponse createStudentRecord(String teacherUsername, CreateStudentRecordRequest request) {
        Teacher teacher = teacherRepository.findByUsername(teacherUsername)
                .orElseThrow(() -> new CustomException(ErrorCode.TEACHER_NOT_FOUND));

        Student student = studentRepository.findById(request.getStudentId())
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        // 동일 학생/학년도/학기 중복 확인
        if (studentRecordRepository.existsByStudentAndYearAndSemester(
                student, request.getYear(), request.getSemester())) {
            throw new CustomException(ErrorCode.STUDENT_RECORD_ALREADY_EXISTS);
        }

        StudentRecord record = studentRecordRepository.save(
                StudentRecord.of(student, teacher,
                        request.getYear(), request.getSemester(),
                        request.getAttendanceDays(), request.getAbsenceDays(),
                        request.getLateDays(), request.getEarlyLeaveDays(),
                        request.getSpecialNote(), request.getVolunteerHours(),
                        request.getRecordDate())
        );

        log.info("학생부 등록 - studentId: {}, year: {}, semester: {}, teacherUsername: {}",
                student.getId(), request.getYear(), request.getSemester(), teacherUsername);

        return toResponse(record);
    }

    /**
     * CEW-30: 기존 학생부 항목 수정
     */
    @Transactional
    public StudentRecordResponse updateStudentRecord(Long recordId, UpdateStudentRecordRequest request) {
        StudentRecord record = studentRecordRepository.findById(recordId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_RECORD_NOT_FOUND));

        record.update(request.getAttendanceDays(), request.getAbsenceDays(),
                request.getLateDays(), request.getEarlyLeaveDays(),
                request.getSpecialNote(), request.getVolunteerHours());

        log.info("학생부 수정 - recordId: {}", recordId);

        return toResponse(record);
    }

    /**
     * CEW-31: 교사가 학생별 학생부 전체 조회
     */
    @Transactional(readOnly = true)
    public List<StudentRecordResponse> getStudentRecords(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new CustomException(ErrorCode.STUDENT_NOT_FOUND));

        return studentRecordRepository.findByStudentOrderByYearDescSemesterDesc(student)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    private StudentRecordResponse toResponse(StudentRecord r) {
        return StudentRecordResponse.builder()
                .id(r.getId())
                .studentId(r.getStudent().getId())
                .studentName(r.getStudent().getName())
                .grade(r.getStudent().getGrade())
                .classNum(r.getStudent().getClassNum())
                .studentNumber(r.getStudent().getStudentNumber())
                .teacherId(r.getTeacher().getId())
                .teacherName(r.getTeacher().getName())
                .year(r.getYear())
                .semester(r.getSemester())
                .recordDate(r.getRecordDate())
                .attendanceDays(r.getAttendanceDays())
                .absenceDays(r.getAbsenceDays())
                .lateDays(r.getLateDays())
                .earlyLeaveDays(r.getEarlyLeaveDays())
                .specialNote(r.getSpecialNote())
                .volunteerHours(r.getVolunteerHours())
                .build();
    }
}
