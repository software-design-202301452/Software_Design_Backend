package inu.lecture.software_design.domain.auth.service;

import inu.lecture.software_design.domain.auth.dto.request.SignupParentRequest;
import inu.lecture.software_design.domain.auth.dto.request.SignupStudentRequest;
import inu.lecture.software_design.domain.auth.dto.request.SignupTeacherRequest;
import inu.lecture.software_design.domain.auth.dto.response.SignupStudentResponse;
import inu.lecture.software_design.domain.parent.entity.Parent;
import inu.lecture.software_design.domain.parent.repository.ParentRepository;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.student.repository.StudentRepository;
import inu.lecture.software_design.domain.teacher.repository.TeacherRepository;
import inu.lecture.software_design.domain.user.repository.UserRepository;
import inu.lecture.software_design.global.exception.CustomException;
import inu.lecture.software_design.global.exception.ErrorCode;
import inu.lecture.software_design.global.security.jwt.JwtTokenProvider;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.lang.reflect.Field;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * CEW-97: 인증/권한 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @InjectMocks
    private AuthService authService;

    @Mock private TeacherRepository teacherRepository;
    @Mock private StudentRepository studentRepository;
    @Mock private ParentRepository parentRepository;
    @Mock private UserRepository userRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtTokenProvider jwtTokenProvider;
    @Mock private AuthenticationManagerBuilder authenticationManagerBuilder;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(authService, "teacherRegistrationCode", "SECRET_CODE");
    }

    // ─── 교사 회원가입 ──────────────────────────────────────────────────────────

    @Test
    void 교사_회원가입_성공() throws Exception {
        // given
        SignupTeacherRequest req = createTeacherRequest("teacher1", "pass", "t@test.com", "홍길동", "수학", "010-1234-5678", "SECRET_CODE");
        when(teacherRepository.existsByUsername(anyString())).thenReturn(false);
        when(studentRepository.existsByUsername(anyString())).thenReturn(false);
        when(parentRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(teacherRepository.existsByEmail(anyString())).thenReturn(false);
        when(studentRepository.existsByEmail(anyString())).thenReturn(false);
        when(parentRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPw");

        // when & then
        authService.signupTeacher(req);
        verify(teacherRepository).save(any());
    }

    @Test
    void 교사_코드_불일치_예외() throws Exception {
        // given
        SignupTeacherRequest req = createTeacherRequest("teacher1", "pass", "t@test.com", "홍길동", "수학", "010-1234-5678", "WRONG_CODE");
        when(teacherRepository.existsByUsername(anyString())).thenReturn(false);
        when(studentRepository.existsByUsername(anyString())).thenReturn(false);
        when(parentRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(teacherRepository.existsByEmail(anyString())).thenReturn(false);
        when(studentRepository.existsByEmail(anyString())).thenReturn(false);
        when(parentRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);

        // when & then
        assertThatThrownBy(() -> authService.signupTeacher(req))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.INVALID_TEACHER_CODE));
        verify(teacherRepository, never()).save(any());
    }

    @Test
    void 중복_username_예외() throws Exception {
        // given
        SignupTeacherRequest req = createTeacherRequest("dup_user", "pass", "t@test.com", "홍길동", "수학", null, "SECRET_CODE");
        when(teacherRepository.existsByUsername("dup_user")).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signupTeacher(req))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.DUPLICATE_USERNAME));
    }

    // ─── 학생 회원가입 ──────────────────────────────────────────────────────────

    @Test
    void 학생_회원가입_성공() throws Exception {
        // given
        SignupStudentRequest req = createStudentRequest("student1", "pass", "s@test.com", "김학생", 2, 3, 15, null, null);
        when(teacherRepository.existsByUsername(anyString())).thenReturn(false);
        when(studentRepository.existsByUsername(anyString())).thenReturn(false);
        when(parentRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(teacherRepository.existsByEmail(anyString())).thenReturn(false);
        when(studentRepository.existsByEmail(anyString())).thenReturn(false);
        when(parentRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(studentRepository.existsByGradeAndClassNumAndStudentNumber(2, 3, 15)).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPw");

        Student mockStudent = Student.of("student1", "encodedPw", "s@test.com", "김학생", 2, 3, 15, null, null);
        setId(mockStudent, 1L);
        when(studentRepository.save(any(Student.class))).thenReturn(mockStudent);

        // when
        SignupStudentResponse response = authService.signupStudent(req);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("김학생");
        assertThat(response.getLinkCode()).isNotNull();
    }

    @Test
    void 중복_학번_예외() throws Exception {
        // given
        SignupStudentRequest req = createStudentRequest("student1", "pass", "s@test.com", "김학생", 2, 3, 15, null, null);
        when(teacherRepository.existsByUsername(anyString())).thenReturn(false);
        when(studentRepository.existsByUsername(anyString())).thenReturn(false);
        when(parentRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(teacherRepository.existsByEmail(anyString())).thenReturn(false);
        when(studentRepository.existsByEmail(anyString())).thenReturn(false);
        when(parentRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(studentRepository.existsByGradeAndClassNumAndStudentNumber(2, 3, 15)).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> authService.signupStudent(req))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.STUDENT_ALREADY_REGISTERED));
    }

    // ─── 학부모 회원가입 ────────────────────────────────────────────────────────

    @Test
    void 학부모_회원가입_성공() throws Exception {
        // given
        Student mockStudent = Student.of("student1", "pw", "s@test.com", "김학생", 2, 3, 15, null, null);
        SignupParentRequest req = createParentRequest("parent1", "pass", "p@test.com", "김부모", mockStudent.getLinkCode(), null);
        when(teacherRepository.existsByUsername(anyString())).thenReturn(false);
        when(studentRepository.existsByUsername(anyString())).thenReturn(false);
        when(parentRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(teacherRepository.existsByEmail(anyString())).thenReturn(false);
        when(studentRepository.existsByEmail(anyString())).thenReturn(false);
        when(parentRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(studentRepository.findByLinkCode(mockStudent.getLinkCode())).thenReturn(Optional.of(mockStudent));
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPw");

        // when & then
        authService.signupParent(req);
        verify(parentRepository).save(any(Parent.class));
    }

    @Test
    void 잘못된_linkCode_예외() throws Exception {
        // given
        SignupParentRequest req = createParentRequest("parent1", "pass", "p@test.com", "김부모", "INVALID_CODE", null);
        when(teacherRepository.existsByUsername(anyString())).thenReturn(false);
        when(studentRepository.existsByUsername(anyString())).thenReturn(false);
        when(parentRepository.existsByUsername(anyString())).thenReturn(false);
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        when(teacherRepository.existsByEmail(anyString())).thenReturn(false);
        when(studentRepository.existsByEmail(anyString())).thenReturn(false);
        when(parentRepository.existsByEmail(anyString())).thenReturn(false);
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        when(studentRepository.findByLinkCode("INVALID_CODE")).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> authService.signupParent(req))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.INVALID_LINK_CODE));
    }

    // ─── 헬퍼 메서드 ───────────────────────────────────────────────────────────

    private void setId(Object entity, Long id) throws Exception {
        Field field = getField(entity.getClass(), "id");
        field.setAccessible(true);
        field.set(entity, id);
    }

    private Field getField(Class<?> clazz, String fieldName) throws NoSuchFieldException {
        try {
            return clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException e) {
            if (clazz.getSuperclass() != null) return getField(clazz.getSuperclass(), fieldName);
            throw e;
        }
    }

    private void setField(Object obj, String fieldName, Object value) throws Exception {
        Field f = obj.getClass().getDeclaredField(fieldName);
        f.setAccessible(true);
        f.set(obj, value);
    }

    private SignupTeacherRequest createTeacherRequest(String username, String password, String email,
                                                       String name, String department, String phone,
                                                       String teacherCode) throws Exception {
        SignupTeacherRequest req = new SignupTeacherRequest();
        setField(req, "username", username);
        setField(req, "password", password);
        setField(req, "email", email);
        setField(req, "name", name);
        setField(req, "department", department);
        setField(req, "phone", phone);
        setField(req, "teacherCode", teacherCode);
        return req;
    }

    private SignupStudentRequest createStudentRequest(String username, String password, String email,
                                                       String name, int grade, int classNum,
                                                       int studentNumber, String phone, String address) throws Exception {
        SignupStudentRequest req = new SignupStudentRequest();
        setField(req, "username", username);
        setField(req, "password", password);
        setField(req, "email", email);
        setField(req, "name", name);
        setField(req, "grade", grade);
        setField(req, "classNum", classNum);
        setField(req, "studentNumber", studentNumber);
        setField(req, "phone", phone);
        setField(req, "address", address);
        return req;
    }

    private SignupParentRequest createParentRequest(String username, String password, String email,
                                                     String name, String studentLinkCode, String phone) throws Exception {
        SignupParentRequest req = new SignupParentRequest();
        setField(req, "username", username);
        setField(req, "password", password);
        setField(req, "email", email);
        setField(req, "name", name);
        setField(req, "studentLinkCode", studentLinkCode);
        setField(req, "phone", phone);
        return req;
    }
}
