package inu.lecture.software_design.domain.auth.dto.response;

import inu.lecture.software_design.domain.user.enums.UserRole;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class LoginResponse {
    private final String accessToken;
    private final String refreshToken;
    private final Long accessTokenExpiresAt;
    private final Long refreshTokenExpiresAt;
    private final String username;
    private final String name;
    private final UserRole role;
}
