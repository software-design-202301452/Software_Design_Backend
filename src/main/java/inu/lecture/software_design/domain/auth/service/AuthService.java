package inu.lecture.software_design.domain.auth.service;

import inu.lecture.software_design.domain.auth.dto.request.LoginRequest;
import inu.lecture.software_design.domain.auth.dto.request.SignupParentRequest;
import inu.lecture.software_design.domain.auth.dto.request.SignupStudentRequest;
import inu.lecture.software_design.domain.auth.dto.request.SignupTeacherRequest;
import inu.lecture.software_design.domain.auth.dto.response.LoginResponse;
import inu.lecture.software_design.domain.auth.dto.response.SignupStudentResponse;
import inu.lecture.software_design.domain.parent.entity.Parent;
import inu.lecture.software_design.domain.parent.repository.ParentRepository;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.student.repository.StudentRepository;
import inu.lecture.software_design.domain.teacher.entity.Teacher;
import inu.lecture.software_design.domain.teacher.repository.TeacherRepository;
import inu.lecture.software_design.domain.user.enums.UserRole;
import inu.lecture.software_design.domain.user.repository.UserRepository;
import inu.lecture.software_design.global.exception.CustomException;
import inu.lecture.software_design.global.exception.ErrorCode;
import inu.lecture.software_design.global.security.jwt.JwtTokenProvider;
import inu.lecture.software_design.global.security.jwt.TokenDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthService {

    private final TeacherRepository teacherRepository;
    private final StudentRepository studentRepository;
    private final ParentRepository parentRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;

    @Value("${teacher.registration-code}")
    private String teacherRegistrationCode;

    /**
     * 교사 회원가입
     * - teacherCode 가 시스템 공통 코드와 일치해야 가입 가능
     */
    @Transactional
    public void signupTeacher(SignupTeacherRequest request) {
        validateDuplicateUsername(request.getUsername());
        validateDuplicateEmail(request.getEmail());

        if (!teacherRegistrationCode.equals(request.getTeacherCode())) {
            throw new CustomException(ErrorCode.INVALID_TEACHER_CODE);
        }

        teacherRepository.save(
                Teacher.of(request.getUsername(),
                        passwordEncoder.encode(request.getPassword()),
                        request.getEmail(),
                        request.getName(),
                        request.getDepartment(),
                        request.getPhone())
        );

        log.info("교사 회원가입 완료 - username: {}", request.getUsername());
    }

    /**
     * 학생 회원가입
     * - 동일한 학년/반/번호 조합이 이미 존재하면 가입 불가
     * - 가입 완료 후 학부모 연동용 linkCode 반환
     */
    @Transactional
    public SignupStudentResponse signupStudent(SignupStudentRequest request) {
        validateDuplicateUsername(request.getUsername());
        validateDuplicateEmail(request.getEmail());

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

        log.info("학생 회원가입 완료 - username: {}, linkCode: {}", request.getUsername(), student.getLinkCode());

        return SignupStudentResponse.builder()
                .studentId(student.getId())
                .name(student.getName())
                .grade(student.getGrade())
                .classNum(student.getClassNum())
                .studentNumber(student.getStudentNumber())
                .linkCode(student.getLinkCode())
                .build();
    }

    /**
     * 학부모 회원가입
     * - 자녀의 linkCode 를 입력하면 해당 학생의 학부모로 등록
     */
    @Transactional
    public void signupParent(SignupParentRequest request) {
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

        log.info("학부모 회원가입 완료 - username: {}, studentId: {}", request.getUsername(), student.getId());
    }

    /**
     * 로그인 (교사/학생/학부모/관리자 공통)
     */
    @Transactional(readOnly = true)
    public LoginResponse login(LoginRequest request) {
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authToken);
        TokenDto tokenDto = jwtTokenProvider.generateTokenDto(authentication);

        String username = request.getUsername();
        String name;
        UserRole role;

        // 어떤 엔티티 타입인지 판별 후 role/name 추출
        var teacher = teacherRepository.findByUsername(username);
        if (teacher.isPresent()) {
            name = teacher.get().getName();
            role = UserRole.TEACHER;
        } else {
            var student = studentRepository.findByUsername(username);
            if (student.isPresent()) {
                name = student.get().getName();
                role = UserRole.STUDENT;
            } else {
                var parent = parentRepository.findByUsername(username);
                if (parent.isPresent()) {
                    name = parent.get().getName();
                    role = UserRole.PARENT;
                } else {
                    var admin = userRepository.findByUsername(username)
                            .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
                    name = admin.getName();
                    role = UserRole.ADMIN;
                }
            }
        }

        return LoginResponse.builder()
                .accessToken(tokenDto.getAccessToken())
                .refreshToken(tokenDto.getRefreshToken())
                .accessTokenExpiresAt(tokenDto.getAccessTokenExpiresAt())
                .refreshTokenExpiresAt(tokenDto.getRefreshTokenExpiresAt())
                .username(username)
                .name(name)
                .role(role)
                .build();
    }

    /** username 전체 테이블 중복 확인 */
    private void validateDuplicateUsername(String username) {
        if (teacherRepository.existsByUsername(username)
                || studentRepository.existsByUsername(username)
                || parentRepository.existsByUsername(username)
                || userRepository.existsByUsername(username)) {
            throw new CustomException(ErrorCode.DUPLICATE_USERNAME);
        }
    }

    /** email 전체 테이블 중복 확인 */
    private void validateDuplicateEmail(String email) {
        if (teacherRepository.existsByEmail(email)
                || studentRepository.existsByEmail(email)
                || parentRepository.existsByEmail(email)
                || userRepository.existsByEmail(email)) {
            throw new CustomException(ErrorCode.DUPLICATE_EMAIL);
        }
    }
}
