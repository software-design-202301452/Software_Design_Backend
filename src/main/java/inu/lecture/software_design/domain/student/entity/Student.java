package inu.lecture.software_design.domain.student.entity;

import inu.lecture.software_design.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table(name = "students")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Student extends BaseEntity {

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

    @Column(nullable = false)
    private Integer grade;

    @Column(nullable = false)
    private Integer classNum;

    @Column(nullable = false)
    private Integer studentNumber;

    @Column(length = 200)
    private String address;

    @Column(length = 20)
    private String phone;

    /**
     * 학부모 연동용 고유 식별 코드 (UUID)
     * - 학생 가입 완료 시 자동 생성되어 응답으로 반환됨
     * - 학부모는 이 코드를 입력해 자녀 계정과 연동
     */
    @Column(nullable = false, unique = true, length = 36)
    private String linkCode;

    private Student(String username, String password, String email, String name,
                    Integer grade, Integer classNum, Integer studentNumber,
                    String phone, String address) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.grade = grade;
        this.classNum = classNum;
        this.studentNumber = studentNumber;
        this.phone = phone;
        this.address = address;
        this.linkCode = UUID.randomUUID().toString();
    }

    public static Student of(String username, String password, String email, String name,
                             Integer grade, Integer classNum, Integer studentNumber,
                             String phone, String address) {
        return new Student(username, password, email, name, grade, classNum, studentNumber, phone, address);
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateInfo(Integer grade, Integer classNum, Integer studentNumber) {
        this.grade = grade;
        this.classNum = classNum;
        this.studentNumber = studentNumber;
    }

    /** 교사가 학생 기본정보(이름, 학년, 반, 번호)를 수정 (CEW-28) */
    public void updateBasicInfo(String name, Integer grade, Integer classNum, Integer studentNumber) {
        this.name = name;
        this.grade = grade;
        this.classNum = classNum;
        this.studentNumber = studentNumber;
    }

    public void updateContact(String phone, String address) {
        this.phone = phone;
        this.address = address;
    }
}
