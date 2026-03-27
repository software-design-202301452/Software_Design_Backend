package inu.lecture.software_design.domain.grade.entity;

import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.subject.entity.Subject;
import inu.lecture.software_design.domain.teacher.entity.Teacher;
import inu.lecture.software_design.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(
    name = "grades",
    uniqueConstraints = @UniqueConstraint(columnNames = {"student_id", "subject_id", "year", "semester"})
)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Grade extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Column(nullable = false)
    private Integer year;

    /** 학기 (1 or 2) */
    @Column(nullable = false)
    private Integer semester;

    @Column(nullable = false)
    private Double score;

    @Column(nullable = false)
    private Double totalScore;

    @Column(nullable = false)
    private Double average;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private GradeLevel gradeLevel;

    @Column(length = 500)
    private String note;

    private Grade(Student student, Subject subject, Teacher teacher,
                  Integer year, Integer semester,
                  Double score, Double totalScore, Double average, String note) {
        this.student = student;
        this.subject = subject;
        this.teacher = teacher;
        this.year = year;
        this.semester = semester;
        this.score = score;
        this.totalScore = totalScore;
        this.average = average;
        this.gradeLevel = GradeLevel.from(score / totalScore * 100);
        this.note = note;
    }

    public static Grade of(Student student, Subject subject, Teacher teacher,
                           Integer year, Integer semester,
                           Double score, Double totalScore, Double average, String note) {
        return new Grade(student, subject, teacher, year, semester, score, totalScore, average, note);
    }

    public void updateScore(Double score, Double totalScore, Double average, String note) {
        this.score = score;
        this.totalScore = totalScore;
        this.average = average;
        this.gradeLevel = GradeLevel.from(score / totalScore * 100);
        this.note = note;
    }
}
