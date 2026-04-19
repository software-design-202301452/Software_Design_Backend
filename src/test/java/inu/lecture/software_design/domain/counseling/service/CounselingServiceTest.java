package inu.lecture.software_design.domain.counseling.service;

import inu.lecture.software_design.domain.counseling.dto.request.CreateCounselingRequest;
import inu.lecture.software_design.domain.counseling.dto.response.CounselingResponse;
import inu.lecture.software_design.domain.counseling.entity.Counseling;
import inu.lecture.software_design.domain.counseling.repository.CounselingRepository;
import inu.lecture.software_design.domain.notification.service.NotificationService;
import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.student.repository.StudentRepository;
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
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * CEW-77: 상담 서비스 단위 테스트
 */
@ExtendWith(MockitoExtension.class)
class CounselingServiceTest {

    @InjectMocks
    private CounselingService counselingService;

    @Mock
    private CounselingRepository counselingRepository;

    @Mock
    private StudentRepository studentRepository;

    @Mock
    private TeacherRepository teacherRepository;

    @Mock
    private NotificationService notificationService;

    private Student mockStudent;
    private Teacher mockTeacher;
    private Counseling mockCounseling;

    @BeforeEach
    void setUp() throws Exception {
        mockStudent = Student.of("student1", "password", "student@test.com",
                "테스트학생", 2, 3, 15, "010-1234-5678", "서울시 강남구");
        setId(mockStudent, 1L);

        mockTeacher = Teacher.of("teacher1", "password", "teacher@test.com",
                "테스트교사", "담임", "010-9876-5432");
        setId(mockTeacher, 1L);

        mockCounseling = Counseling.of(
                mockStudent, mockTeacher,
                LocalDate.of(2024, 3, 15),
                "학습 태도 개선에 대해 상담하였습니다.",
                "다음 상담까지 수학 복습",
                LocalDate.of(2024, 4, 15)
        );
        setId(mockCounseling, 1L);
    }

    @Test
    void 상담_등록_성공() throws Exception {
        // given
        CreateCounselingRequest request = createCounselingRequest(
                1L, LocalDate.of(2024, 3, 15),
                "학습 태도 개선에 대해 상담하였습니다.",
                "다음 상담까지 수학 복습",
                LocalDate.of(2024, 4, 15)
        );

        when(teacherRepository.findByUsername("teacher1")).thenReturn(Optional.of(mockTeacher));
        when(studentRepository.findById(1L)).thenReturn(Optional.of(mockStudent));
        when(counselingRepository.save(any(Counseling.class))).thenReturn(mockCounseling);
        doNothing().when(notificationService).notifyCounselingUpdated(any(), any(), any());

        // when
        CounselingResponse response = counselingService.createCounseling("teacher1", request);

        // then
        assertThat(response).isNotNull();
        assertThat(response.getStudentId()).isEqualTo(1L);
        assertThat(response.getTeacherName()).isEqualTo("테스트교사");
        assertThat(response.getCounselingDate()).isEqualTo(LocalDate.of(2024, 3, 15));
        assertThat(response.isShared()).isFalse();
        verify(counselingRepository).save(any(Counseling.class));
        verify(notificationService).notifyCounselingUpdated(any(), any(), any());
    }

    @Test
    void 학생별_상담_조회() {
        // given
        when(studentRepository.findById(1L)).thenReturn(Optional.of(mockStudent));
        when(counselingRepository.findByStudentWithDateFilter(mockStudent, null, null))
                .thenReturn(List.of(mockCounseling));

        // when
        List<CounselingResponse> responses = counselingService.getCounselingsByStudent(1L, null, null);

        // then
        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).getContent()).isEqualTo("학습 태도 개선에 대해 상담하였습니다.");
    }

    @Test
    void 상담_공유_설정_성공() {
        // given
        when(counselingRepository.findById(1L)).thenReturn(Optional.of(mockCounseling));

        // when
        CounselingResponse response = counselingService.shareCounseling(1L);

        // then
        assertThat(response.isShared()).isTrue();
    }

    @Test
    void 상담_삭제_성공() {
        // given
        when(counselingRepository.findById(1L)).thenReturn(Optional.of(mockCounseling));

        // when
        counselingService.deleteCounseling(1L);

        // then
        verify(counselingRepository).delete(mockCounseling);
    }

    @Test
    void 존재하지_않는_상담_삭제_예외() {
        // given
        when(counselingRepository.findById(999L)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> counselingService.deleteCounseling(999L))
                .isInstanceOf(CustomException.class)
                .satisfies(ex -> assertThat(((CustomException) ex).getErrorCode())
                        .isEqualTo(ErrorCode.COUNSELING_NOT_FOUND));
    }

    @Test
    void 공유_상담_목록_조회() {
        // given
        when(counselingRepository.findBySharedTrueOrderByCounselingDateDesc())
                .thenReturn(List.of(mockCounseling));

        // when
        List<CounselingResponse> responses = counselingService.getSharedCounselings();

        // then
        assertThat(responses).hasSize(1);
        verify(counselingRepository).findBySharedTrueOrderByCounselingDateDesc();
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

    private CreateCounselingRequest createCounselingRequest(Long studentId, LocalDate counselingDate,
                                                             String content, String nextPlan,
                                                             LocalDate nextCounselingDate) throws Exception {
        CreateCounselingRequest req = new CreateCounselingRequest();
        setField(req, "studentId", studentId);
        setField(req, "counselingDate", counselingDate);
        setField(req, "content", content);
        setField(req, "nextPlan", nextPlan);
        setField(req, "nextCounselingDate", nextCounselingDate);
        return req;
    }

    private void setField(Object obj, String fieldName, Object value) throws Exception {
        Field field = obj.getClass().getDeclaredField(fieldName);
        field.setAccessible(true);
        field.set(obj, value);
    }
}
