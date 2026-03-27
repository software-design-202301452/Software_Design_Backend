package inu.lecture.software_design.global.security.service;

import inu.lecture.software_design.domain.parent.repository.ParentRepository;
import inu.lecture.software_design.domain.student.repository.StudentRepository;
import inu.lecture.software_design.domain.teacher.repository.TeacherRepository;
import inu.lecture.software_design.domain.user.repository.UserRepository;
import inu.lecture.software_design.global.exception.CustomException;
import inu.lecture.software_design.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final UserRepository userRepository;  // admin

    /**
     * username으로 Teacher → Student → Parent → Admin(User) 순서로 탐색
     */
    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 1. 교사 테이블 조회
        var teacher = teacherRepository.findByUsername(username);
        if (teacher.isPresent()) {
            return buildUserDetails(teacher.get().getUsername(),
                    teacher.get().getPassword(), "ROLE_TEACHER");
        }

        // 2. 학생 테이블 조회
        var student = studentRepository.findByUsername(username);
        if (student.isPresent()) {
            return buildUserDetails(student.get().getUsername(),
                    student.get().getPassword(), "ROLE_STUDENT");
        }

        // 3. 학부모 테이블 조회
        var parent = parentRepository.findByUsername(username);
        if (parent.isPresent()) {
            return buildUserDetails(parent.get().getUsername(),
                    parent.get().getPassword(), "ROLE_PARENT");
        }

        // 4. 관리자 테이블 조회
        var admin = userRepository.findByUsername(username);
        if (admin.isPresent()) {
            return buildUserDetails(admin.get().getUsername(),
                    admin.get().getPassword(), "ROLE_ADMIN");
        }

        throw new CustomException(ErrorCode.USER_NOT_FOUND);
    }

    private UserDetails buildUserDetails(String username, String password, String role) {
        return org.springframework.security.core.userdetails.User.builder()
                .username(username)
                .password(password)
                .authorities(List.of(new SimpleGrantedAuthority(role)))
                .build();
    }
}
