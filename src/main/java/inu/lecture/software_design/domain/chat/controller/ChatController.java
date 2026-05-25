package inu.lecture.software_design.domain.chat.controller;

import inu.lecture.software_design.domain.chat.dto.ChatRequest;
import inu.lecture.software_design.domain.chat.dto.ChatResponse;
import inu.lecture.software_design.domain.chat.service.ChatService;
import inu.lecture.software_design.global.response.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * CEW-117: AI 챗봇 API
 * POST /api/v1/chat — 학생 학습 요약 데이터를 컨텍스트로 AI와 대화
 */
@RestController
@RequestMapping("/api/v1/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ApiResponse<ChatResponse>> chat(@Valid @RequestBody ChatRequest request) {
        return ResponseEntity.ok(ApiResponse.success(chatService.chat(request)));
    }
}
