package inu.lecture.software_design.domain.parent.entity;

import inu.lecture.software_design.domain.student.entity.Student;
import inu.lecture.software_design.global.entity.BaseEntity;
import inu.lecture.software_design.global.security.encryption.EncryptionConverter;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "parents")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Parent extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String password;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false, length = 50)
    private String name;

    /** 자녀 학생 (한 부모 계정은 한 자녀와 연동) */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @Convert(converter = EncryptionConverter.class)
    @Column(length = 100)
    private String phone;

    private Parent(String username, String password, String email, String name,
                   Student student, String phone) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.student = student;
        this.phone = phone;
    }

    public static Parent of(String username, String password, String email, String name,
                            Student student, String phone) {
        return new Parent(username, password, email, name, student, phone);
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updatePhone(String phone) {
        this.phone = phone;
    }
}
