package inu.lecture.software_design.domain.user.repository;

import inu.lecture.software_design.domain.user.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

/** 관리자(ADMIN) 계정 전용 Repository */
public interface UserRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
}
