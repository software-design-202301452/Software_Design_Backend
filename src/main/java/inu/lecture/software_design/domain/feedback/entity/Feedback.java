package inu.lecture.software_design.domain.feedback.entity;

import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.domain.teacher.entity.Teacher;
import inu.lecture.software_design.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "feedbacks")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Feedback extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "teacher_id", nullable = false)
    private Teacher teacher;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FeedbackType feedbackType;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    /** 학생/학부모에게 공개 여부 */
    @Column(nullable = false)
    private boolean published;

    private Feedback(Student student, Teacher teacher, FeedbackType feedbackType, String content) {
        this.student = student;
        this.teacher = teacher;
        this.feedbackType = feedbackType;
        this.content = content;
        this.published = false;
    }

    public static Feedback of(Student student, Teacher teacher,
                              FeedbackType feedbackType, String content) {
        return new Feedback(student, teacher, feedbackType, content);
    }

    public void update(FeedbackType feedbackType, String content) {
        this.feedbackType = feedbackType;
        this.content = content;
    }

    public void publish() { this.published = true; }
    public void unpublish() { this.published = false; }
}
