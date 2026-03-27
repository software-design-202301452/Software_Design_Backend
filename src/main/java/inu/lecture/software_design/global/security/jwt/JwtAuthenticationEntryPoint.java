package inu.lecture.software_design.global.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;
import inu.lecture.software_design.global.exception.ErrorCode;
import inu.lecture.software_design.global.exception.ErrorResponseEntity;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    public JwtAuthenticationEntryPoint() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {

        log.warn("인증 실패 - URI: {}, Message: {}", request.getRequestURI(), authException.getMessage());

        ErrorCode errorCode = ErrorCode.JWT_ENTRY_POINT;

        response.setContentType("application/json;charset=UTF-8");
        response.setStatus(errorCode.getHttpStatus().value());

        ErrorResponseEntity errorResponse = ErrorResponseEntity.builder()
                .code(errorCode.getCode())
                .name(errorCode.name())
                .message(errorCode.getMessage())
                .build();

        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }
}