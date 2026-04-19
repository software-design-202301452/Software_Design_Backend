package inu.lecture.software_design.domain.grade.service;

import inu.lecture.software_design.domain.grade.dto.request.CreateGradeRequest;
import inu.lecture.software_design.domain.grade.dto.response.GradeResponse;
import inu.lecture.software_design.domain.grade.entity.Grade;
import inu.lecture.software_design.domain.grade.entity.GradeLevel;
import inu.lecture.software_design.domain.grade.repository.GradeRepository;
import inu.lecture.software_design.domain.notification.service.NotificationService;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.student.repository.StudentRepository;
import inu.lecture.software_design.domain.subject.entity.Subject;
import inu.lecture.software_design.domain.subject.repository.SubjectRepository;
import inu.lecture.software_design.domain.teacher.entity.Teacher;
import inu.lecture.software_design.domain.teacher.repository.TeacherRepository;
import inu.lecture.software_design.global.exception.CustomException;
import inu.lecture.software_design.global.exception.ErrorCode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CEW-77: 성적 서비스 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class GradeServiceTest {

    @InjectMocks
    private GradeService gradeService;

    @Mock
    private GradeRepository gradeRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private SubjectRepository subjectRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private NotificationService notificationService;

    private Student mockStudent;
    private Teacher mockTeacher;
    private Subject mockSubject;
    private Grade mockGrade;

    @BeforeEach
    void setUp() throws Exception {
        mockStudent = Student.of("student1", "password", "student@test.com",
                "테스트학생", 2, 3, 15, "010-1234-5678", "서울시 강남구");
        setId(mockStudent, 1L);

        mockTeacher = Teacher.of("teacher1", "password", "teacher@test.com",
                "테스트교사", "수학", "010-9876-5432");
        setId(mockTeacher, 1L);

        mockSubject = Subject.of("수학", "수학 과목");
        setId(mockSubject, 1L);

        mockGrade = Grade.of(mockStudent, mockSubject, mockTeacher, 2024, 1, 85.0, 100.0, 85.0, "테스트 비고");
        setId(mockGrade, 1L);
    }

    @Test
    void 성적_등록_성공() throws Exception {
        // given
        CreateGradeRequest request = createGradeRequest(1L, 1L, 2024, 1, 85.0, 100.0, null);

        when(teacherRepository.findByUsername("teacher1")).thenReturn(Optional.of(mockTeacher));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(mockStudent));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mockSubject));
        when(gradeRepository.existsByStudentAndSubjectAndYearAndSemester(any(), any(), any(), any())).thenReturn(false);
        when(gradeRepository.save(any(Grade.class))).thenReturn(mockGrade);
        doNothing().when(notificationService).notifyGradeUpdated(any(), any());

        // when
        GradeResponse response = gradeService.createGrade("teacher1", request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStudentId()).isEqualTo(1L);
        assertThat(response.getSubjectName()).isEqualTo("수학");
        assertThat(response.getGradeLevel()).isEqualTo(GradeLevel.B);
        verify(gradeRepository).save(any(Grade.class));
        verify(notificationService).notifyGradeUpdated(any(), any());
    }

    @Test
    void 중복_성적_등록_예외() throws Exception {
        // given
        CreateGradeRequest request = createGradeRequest(1L, 1L, 2024, 1, 85.0, 100.0, null);

        when(teacherRepository.findByUsername("teacher1")).thenReturn(Optional.of(mockTeacher));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(mockStudent));
        when(subjectRepository.findById(1L)).thenReturn(Optional.of(mockSubject));
        when(gradeRepository.existsByStudentAndSubjectAndYearAndSemester(any(), any(), any(), any())).thenReturn(true);

        // when & then
        assertThatThrownBy(() -> gradeService.createGrade("teacher1", request))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.GRADE_ALREADY_EXISTS));
        verify(gradeRepository, never()).save(any());
    }

    @Test
    void 학생별_성적_조회() throws Exception {
        // given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(mockStudent));
        when(gradeRepository.findByStudentOrderByYearDescSemesterAsc(mockStudent))
                .thenReturn(List.of(mockGrade));

        // when
        List<GradeResponse> responses = gradeService.getGradesByStudent(1L);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getSubjectName()).isEqualTo("수학");
        assertThat(responses.get(0).getYear()).isEqualTo(2024);
        verify(gradeRepository).findByStudentOrderByYearDescSemesterAsc(mockStudent);
    }

    @Test
    void 존재하지_않는_학생_성적_조회_예외() {
        // given
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> gradeService.getGradesByStudent(999L))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.STUDENT_NOT_FOUND));
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

    private CreateGradeRequest createGradeRequest(Long studentId, Long subjectId,
                                                   Integer year, Integer semester,
                                                   Double score, Double totalScore, String note)
            throws Exception {
        CreateGradeRequest req = new CreateGradeRequest();
        setField(req, "studentId", studentId);
        setField(req, "subjectId", subjectId);
        setField(req, "year", year);
        setField(req, "semester", semester);
        setField(req, "score", score);
        setField(req, "totalScore", totalScore);
        setField(req, "note", note);
        return req;
    }

    private void setField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}
