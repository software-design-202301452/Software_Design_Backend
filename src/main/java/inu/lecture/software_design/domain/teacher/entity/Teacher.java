package inu.lecture.software_design.domain.teacher.entity;

import inu.lecture.software_design.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "teachers")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Teacher extends BaseEntity {

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

    /** 담당 과목/부서 */
    @Column(length = 100)
    private String department;

    /** 연락처 */
    @Column(length = 20)
    private String phone;

    private Teacher(String username, String password, String email, String name,
                    String department, String phone) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
        this.department = department;
        this.phone = phone;
    }

    public static Teacher of(String username, String password, String email, String name,
                             String department, String phone) {
        return new Teacher(username, password, email, name, department, phone);
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateInfo(String department, String phone) {
        this.department = department;
        this.phone = phone;
    }
}
