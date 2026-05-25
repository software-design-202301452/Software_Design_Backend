package inu.lecture.software_design.domain.chat.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class ChatRequest {
    private Long studentId;   // 선택 — 학생 컨텍스트 주입용
    @NotBlank
    private String message;
}
