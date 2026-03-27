package inu.lecture.software_design.domain.user.entity;

import inu.lecture.software_design.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 관리자 전용 계정 엔티티
 * - 교사/학생/학부모는 각자의 엔티티(Teacher/Student/Parent)에서 직접 인증 정보 관리
 */
@Entity
@Table(name = "admins")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends BaseEntity {

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

    private Admin(String username, String password, String email, String name) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.name = name;
    }

    public static Admin ofAdmin(String username, String password, String email, String name) {
        return new Admin(username, password, email, name);
    }

    public void updatePassword(String encodedPassword) {
        this.password = encodedPassword;
    }

    public void updateEmail(String email) {
        this.email = email;
    }
}
