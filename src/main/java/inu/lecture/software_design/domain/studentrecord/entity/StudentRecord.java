package inu.lecture.software_design.domain.studentrecord.entity;

import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.teacher.entity.Teacher;
import inu.lecture.software_design.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "student_records")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StudentRecord extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    /** 학년도 */
    @Column(nullable = false)
    private Integer year;

    /** 학기 (1 or 2) */
    @Column(nullable = false)
    private Integer semester;

    /** 출석일수 */
    private Integer attendanceDays;

    /** 결석일수 */
    private Integer absenceDays;

    /** 지각횟수 */
    private Integer lateDays;

    /** 조퇴횟수 */
    private Integer earlyLeaveDays;

    /** 특기사항 */
    @Column(columnDefinition = "TEXT")
    private String specialNote;

    /** 봉사활동 시간 */
    private Integer volunteerHours;

    @Column(nullable = false)
    private LocalDate recordDate;

    private StudentRecord(Student student, Teacher teacher, Integer year, Integer semester,
                          Integer attendanceDays, Integer absenceDays, Integer lateDays,
                          Integer earlyLeaveDays, String specialNote, Integer volunteerHours,
                          LocalDate recordDate) {
        this.student = student;
        this.teacher = teacher;
        this.year = year;
        this.semester = semester;
        this.attendanceDays = attendanceDays;
        this.absenceDays = absenceDays;
        this.lateDays = lateDays;
        this.earlyLeaveDays = earlyLeaveDays;
        this.specialNote = specialNote;
        this.volunteerHours = volunteerHours;
        this.recordDate = recordDate;
    }

    public static StudentRecord of(Student student, Teacher teacher, Integer year, Integer semester,
                                   Integer attendanceDays, Integer absenceDays, Integer lateDays,
                                   Integer earlyLeaveDays, String specialNote, Integer volunteerHours,
                                   LocalDate recordDate) {
        return new StudentRecord(student, teacher, year, semester,
                attendanceDays, absenceDays, lateDays, earlyLeaveDays,
                specialNote, volunteerHours, recordDate);
    }

    public void update(Integer attendanceDays, Integer absenceDays, Integer lateDays,
                       Integer earlyLeaveDays, String specialNote, Integer volunteerHours) {
        this.attendanceDays = attendanceDays;
        this.absenceDays = absenceDays;
        this.lateDays = lateDays;
        this.earlyLeaveDays = earlyLeaveDays;
        this.specialNote = specialNote;
        this.volunteerHours = volunteerHours;
    }
}
