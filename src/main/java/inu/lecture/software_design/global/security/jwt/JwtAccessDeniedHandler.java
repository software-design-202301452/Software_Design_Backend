package inu.lecture.software_design.global.security.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import inu.lecture.software_design.global.exception.ErrorCode;
import inu.lecture.software_design.global.exception.ErrorResponseEntity;
import tools.jackson.databind.ObjectMapper;

import java.io.IOException;

@Slf4j
@Component
public class JwtAccessDeniedHandler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    public JwtAccessDeniedHandler() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {

        log.warn("접근 거부 - URI: {}, Message: {}", request.getRequestURI(), accessDeniedException.getMessage());

        ErrorCode errorCode = ErrorCode.JWT_ACCESS_DENIED;

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