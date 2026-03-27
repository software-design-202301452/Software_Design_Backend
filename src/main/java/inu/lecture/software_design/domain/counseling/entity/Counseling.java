package inu.lecture.software_design.domain.counseling.entity;

import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.teacher.entity.Teacher;
import inu.lecture.software_design.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "counselings")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Counseling extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Column(nullable = false)
    private LocalDate counselingDate;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 다음 상담 계획 */
    @Column(columnDefinition = "TEXT")
    private String nextPlan;

    /** 다음 상담 예정일 */
    private LocalDate nextCounselingDate;

    /** 다른 교사와 공유 여부 */
    @Column(nullable = false)
    private boolean shared;

    private Counseling(Student student, Teacher teacher, LocalDate counselingDate,
                       String content, String nextPlan, LocalDate nextCounselingDate) {
        this.student = student;
        this.teacher = teacher;
        this.counselingDate = counselingDate;
        this.content = content;
        this.nextPlan = nextPlan;
        this.nextCounselingDate = nextCounselingDate;
        this.shared = false;
    }

    public static Counseling of(Student student, Teacher teacher, LocalDate counselingDate,
                                String content, String nextPlan, LocalDate nextCounselingDate) {
        return new Counseling(student, teacher, counselingDate, content, nextPlan, nextCounselingDate);
    }

    public void update(String content, String nextPlan, LocalDate nextCounselingDate) {
        this.content = content;
        this.nextPlan = nextPlan;
        this.nextCounselingDate = nextCounselingDate;
    }

    public void share() { this.shared = true; }
    public void unshare() { this.shared = false; }
}
