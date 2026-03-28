package inu.lecture.software_design.domain.admin.dto.response;

import inu.lecture.software_design.domain.user.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
@Builder
public class AccountListResponse {

    private List<AccountSummary> teachers;
    private List<AccountSummary> students;
    private List<AccountSummary> parents;
    private List<AccountSummary> admins;

    @Getter
    @Builder
    public static class AccountSummary {
        private Long id;
        private String username;
        private String name;
        private String email;
        private UserRole role;
        private String extra; // 교사: 부서, 학생: 학년/반/번호, 학부모: 자녀 이름
    }
}
