package inu.lecture.software_design.domain.admin.service;

import inu.lecture.software_design.domain.admin.dto.request.AdminCreateParentRequest;
import inu.lecture.software_design.domain.admin.dto.request.AdminCreateStudentRequest;
import inu.lecture.software_design.domain.admin.dto.request.AdminCreateTeacherRequest;
import inu.lecture.software_design.domain.admin.dto.response.AccountListResponse;
import inu.lecture.software_design.domain.admin.dto.response.AccountListResponse.AccountSummary;
import inu.lecture.software_design.domain.parent.entity.Parent;
import inu.lecture.software_design.domain.parent.repository.ParentRepository;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.student.repository.StudentRepository;
import inu.lecture.software_design.domain.teacher.entity.Teacher;
import inu.lecture.software_design.domain.teacher.repository.TeacherRepository;
import inu.lecture.software_design.domain.user.entity.UserRole;
import inu.lecture.software_design.domain.user.repository.UserRepository;
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
public class AdminService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 전체 계정 목록 조회 (교사/학생/학부모/관리자)
     */
    @Transactional(readOnly = true)
    public AccountListResponse getAllAccounts() {
        List<AccountSummary> teachers = teacherRepository.findAll().stream()
                .map(t -> AccountSummary.builder()
                        .id(t.getId())
                        .username(t.getUsername())
                        .name(t.getName())
                        .email(t.getEmail())
                        .role(UserRole.TEACHER)
                        .extra(t.getDepartment())
                        .build())
                .toList();

        List<AccountSummary> students = studentRepository.findAll().stream()
                .map(s -> AccountSummary.builder()
                        .id(s.getId())
                        .username(s.getUsername())
                        .name(s.getName())
                        .email(s.getEmail())
                        .role(UserRole.STUDENT)
                        .extra(s.getGrade() + "학년 " + s.getClassNum() + "반 " + s.getStudentNumber() + "번")
                        .build())
                .toList();

        List<AccountSummary> parents = parentRepository.findAll().stream()
                .map(p -> AccountSummary.builder()
                        .id(p.getId())
                        .username(p.getUsername())
                        .name(p.getName())
                        .email(p.getEmail())
                        .role(UserRole.PARENT)
                        .extra("자녀: " + p.getStudent().getName())
                        .build())
                .toList();

        List<AccountSummary> admins = userRepository.findAll().stream()
                .map(a -> AccountSummary.builder()
                        .id(a.getId())
                        .username(a.getUsername())
                        .name(a.getName())
                        .email(a.getEmail())
                        .role(UserRole.ADMIN)
                        .build())
                .toList();

        return AccountListResponse.builder()
                .teachers(teachers)
                .students(students)
                .parents(parents)
                .admins(admins)
                .build();
    }

    /**
     * 교사 계정 생성 (관리자 권한 - teacherCode 불필요)
     */
    @Transactional
    public void createTeacher(AdminCreateTeacherRequest request) {
        validateDuplicateUsername(request.getUsername());
        validateDuplicateEmail(request.getEmail());

        teacherRepository.save(
                Teacher.of(request.getUsername(),
                        passwordEncoder.encode(request.getPassword()),
                        request.getEmail(),
                        request.getName(),
                        request.getDepartment(),
                        request.getPhone())
        );

        log.info("관리자가 교사 계정 생성 - username: {}", request.getUsername());
    }

    /**
     * 학생 계정 생성 (관리자 권한)
     */
    @Transactional
    public void createStudent(AdminCreateStudentRequest request) {
        validateDuplicateUsername(request.getUsername());
        validateDuplicateEmail(request.getEmail());

        if (studentRepository.existsByGradeAndClassNumAndStudentNumber(
                request.getGrade(), request.getClassNum(), request.getStudentNumber())) {
            throw new CustomException(ErrorCode.STUDENT_ALREADY_REGISTERED);
        }

        studentRepository.save(
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

        log.info("관리자가 학생 계정 생성 - username: {}", request.getUsername());
    }

    /**
     * 학부모 계정 생성 (관리자 권한)
     */
    @Transactional
    public void createParent(AdminCreateParentRequest request) {
        validateDuplicateUsername(request.getUsername());
        validateDuplicateEmail(request.getEmail());

        Student student = studentRepository.findByLinkCode(request.getStudentLinkCode())
                .orElseThrow(() -> new CustomException(ErrorCode.INVALID_LINK_CODE));

        parentRepository.save(
                Parent.of(request.getUsername(),
                        passwordEncoder.encode(request.getPassword()),
                        request.getEmail(),
                        request.getName(),
                        student,
                        request.getPhone())
        );

        log.info("관리자가 학부모 계정 생성 - username: {}, studentId: {}", request.getUsername(), student.getId());
    }

    private void validateDuplicateUsername(String username) {
        if (teacherRepository.existsByUsername(username)
                || studentRepository.existsByUsername(username)
                || parentRepository.existsByUsername(username)
                || userRepository.existsByUsername(username)) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
    }

    private void validateDuplicateEmail(String email) {
        if (teacherRepository.existsByEmail(email)
                || studentRepository.existsByEmail(email)
                || parentRepository.existsByEmail(email)
                || userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
    }
}
