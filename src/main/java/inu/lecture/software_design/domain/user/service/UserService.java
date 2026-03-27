package inu.lecture.software_design.domain.user.service;

import inu.lecture.software_design.domain.parent.entity.Parent;
import inu.lecture.software_design.domain.parent.repository.ParentRepository;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.student.repository.StudentRepository;
import inu.lecture.software_design.domain.teacher.entity.Teacher;
import inu.lecture.software_design.domain.teacher.repository.TeacherRepository;
import inu.lecture.software_design.domain.user.dto.request.UpdateMyInfoRequest;
import inu.lecture.software_design.domain.user.dto.request.UpdatePasswordRequest;
import inu.lecture.software_design.domain.user.dto.response.MyInfoResponse;
import inu.lecture.software_design.domain.user.entity.Admin;
import inu.lecture.software_design.domain.user.entity.UserRole;
import inu.lecture.software_design.domain.user.repository.UserRepository;
import inu.lecture.software_design.global.exception.CustomException;
import inu.lecture.software_design.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    /**
     * 본인 정보 조회 - Teacher → Student → Parent → Admin 순으로 탐색
     */
    @Transactional(readOnly = true)
    public MyInfoResponse getMyInfo(String username) {
        var teacher = teacherRepository.findByUsername(username);
        if (teacher.isPresent()) {
            return toTeacherResponse(teacher.get());
        }

        var student = studentRepository.findByUsername(username);
        if (student.isPresent()) {
            return toStudentResponse(student.get());
        }

        var parent = parentRepository.findByUsername(username);
        if (parent.isPresent()) {
            return toParentResponse(parent.get());
        }

        Admin admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        return toAdminResponse(admin);
    }

    /**
     * 본인 정보 수정 - 역할별 수정 가능한 필드 처리
     * 교사: department, phone
     * 학생: phone, address
     * 학부모: phone
     * 관리자: 수정 불가 (별도 관리)
     */
    @Transactional
    public void updateMyInfo(String username, UpdateMyInfoRequest request) {
        var teacher = teacherRepository.findByUsername(username);
        if (teacher.isPresent()) {
            teacher.get().updateInfo(request.getDepartment(), request.getPhone());
            return;
        }

        var student = studentRepository.findByUsername(username);
        if (student.isPresent()) {
            student.get().updateContact(request.getPhone(), request.getAddress());
            return;
        }

        var parent = parentRepository.findByUsername(username);
        if (parent.isPresent()) {
            parent.get().updatePhone(request.getPhone());
            return;
        }

        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

    /**
     * 비밀번호 변경 - 현재 비밀번호 확인 후 변경
     */
    @Transactional
    public void updatePassword(String username, UpdatePasswordRequest request) {
        var teacher = teacherRepository.findByUsername(username);
        if (teacher.isPresent()) {
            validateCurrentPassword(request.getCurrentPassword(), teacher.get().getPassword());
            teacher.get().updatePassword(passwordEncoder.encode(request.getNewPassword()));
            return;
        }

        var student = studentRepository.findByUsername(username);
        if (student.isPresent()) {
            validateCurrentPassword(request.getCurrentPassword(), student.get().getPassword());
            student.get().updatePassword(passwordEncoder.encode(request.getNewPassword()));
            return;
        }

        var parent = parentRepository.findByUsername(username);
        if (parent.isPresent()) {
            validateCurrentPassword(request.getCurrentPassword(), parent.get().getPassword());
            parent.get().updatePassword(passwordEncoder.encode(request.getNewPassword()));
            return;
        }

        Admin admin = userRepository.findByUsername(username)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        validateCurrentPassword(request.getCurrentPassword(), admin.getPassword());
        admin.updatePassword(passwordEncoder.encode(request.getNewPassword()));
    }

    private void validateCurrentPassword(String raw, String encoded) {
        if (!passwordEncoder.matches(raw, encoded)) {
            throw new CustomException(ErrorCode.INVALID_PASSWORD);
        }
    }

    private MyInfoResponse toTeacherResponse(Teacher t) {
        return MyInfoResponse.builder()
                .id(t.getId())
                .username(t.getUsername())
                .name(t.getName())
                .email(t.getEmail())
                .role(UserRole.TEACHER)
                .phone(t.getPhone())
                .department(t.getDepartment())
                .build();
    }

    private MyInfoResponse toStudentResponse(Student s) {
        return MyInfoResponse.builder()
                .id(s.getId())
                .username(s.getUsername())
                .name(s.getName())
                .email(s.getEmail())
                .role(UserRole.STUDENT)
                .phone(s.getPhone())
                .address(s.getAddress())
                .grade(s.getGrade())
                .classNum(s.getClassNum())
                .studentNumber(s.getStudentNumber())
                .build();
    }

    private MyInfoResponse toParentResponse(Parent p) {
        Student child = p.getStudent();
        return MyInfoResponse.builder()
                .id(p.getId())
                .username(p.getUsername())
                .name(p.getName())
                .email(p.getEmail())
                .role(UserRole.PARENT)
                .phone(p.getPhone())
                .studentName(child.getName())
                .studentGrade(child.getGrade())
                .studentClassNum(child.getClassNum())
                .build();
    }

    private MyInfoResponse toAdminResponse(Admin a) {
        return MyInfoResponse.builder()
                .id(a.getId())
                .username(a.getUsername())
                .name(a.getName())
                .email(a.getEmail())
                .role(UserRole.ADMIN)
                .build();
    }
}
