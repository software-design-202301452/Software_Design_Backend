package inu.lecture.software_design.domain.chat.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ChatResponse {
    private String reply;
    private String studentContext;  // 주입된 학생 컨텍스트 요약 (선택)
}
