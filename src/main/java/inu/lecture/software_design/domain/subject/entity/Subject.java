package inu.lecture.software_design.domain.subject.entity;

import inu.lecture.software_design.global.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "subjects")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Subject extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 300)
    private String description;

    private Subject(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static Subject of(String name, String description) {
        return new Subject(name, description);
    }

    public void update(String name, String description) {
        this.name = name;
        this.description = description;
    }
}
