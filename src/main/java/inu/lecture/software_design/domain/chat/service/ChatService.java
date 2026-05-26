package inu.lecture.software_design.domain.chat.service;

import inu.lecture.software_design.domain.analytics.entity.StudentLearningSummary;
import inu.lecture.software_design.domain.analytics.repository.StudentLearningSummaryRepository;
import inu.lecture.software_design.domain.chat.dto.ChatRequest;
import inu.lecture.software_design.domain.chat.dto.ChatResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * CEW-117: AI 챗봇 서비스
 * AI:NU Anthropic 엔드포인트를 통해 Claude와 대화한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final StudentLearningSummaryRepository summaryRepository;
    private final RestTemplate restTemplate;

    @Value("${openai.api-key:}")
    private String apiKey;

    private static final String ANTHROPIC_URL = "https://factchat-cloud.mindlogic.ai/v1/api/anthropic/messages";
    private static final String MODEL = "claude-sonnet-4-5-20250929";
    private static final String SYSTEM_PROMPT_BASE =
            "당신은 학생 관리 시스템의 AI 보조 교사입니다. " +
            "아래 규칙을 반드시 지켜주세요:\n" +
            "1. 제공된 학생 데이터(평균, 성적 수, 피드백 수, 상담 횟수)만으로 바로 분석하고 답변하세요.\n" +
            "2. '추가 정보가 필요합니다', '상세 데이터가 있다면', '더 분석하기 어렵습니다' 같은 표현을 절대 사용하지 마세요.\n" +
            "3. 데이터가 부족해도 주어진 정보로 할 수 있는 분석과 조언을 제공하세요.\n" +
            "4. 답변은 항상 한국어로 하며 간결하고 실용적으로 작성하세요.";

    public ChatResponse chat(ChatRequest request) {
        String systemPrompt = buildSystemPrompt(request.getStudentId());
        String contextDescription = buildContextDescription(request.getStudentId());
        String reply = callClaude(systemPrompt, request.getMessage());
        return ChatResponse.builder()
                .reply(reply)
                .studentContext(contextDescription)
                .build();
    }

    private String buildSystemPrompt(Long studentId) {
        StringBuilder sb = new StringBuilder(SYSTEM_PROMPT_BASE);
        if (studentId != null) {
            Optional<StudentLearningSummary> summaryOpt = summaryRepository.findByStudentId(studentId);
            summaryOpt.ifPresent(s -> {
                sb.append("\n\n[현재 선택된 학생 정보]\n");
                sb.append(String.format("이름: %s\n", s.getStudentName()));
                sb.append(String.format("학년/반/번호: %d학년 %d반 %d번\n", s.getGrade(), s.getClassNum(), s.getStudentNumber()));
                sb.append(String.format("전체 성적 평균: %.1f점\n", s.getOverallAverage()));
                sb.append(String.format("등록된 성적 수: %d개\n", s.getGradeCount()));
                sb.append(String.format("피드백 수: %d건\n", s.getFeedbackCount()));
                sb.append(String.format("상담 횟수: %d회\n", s.getCounselingCount()));
                sb.append(String.format("마지막 데이터 갱신: %s\n", s.getLastSyncedAt()));
            });
        }
        return sb.toString();
    }

    private String buildContextDescription(Long studentId) {
        if (studentId == null) return null;
        return summaryRepository.findByStudentId(studentId)
                .map(s -> String.format("%s (%d학년 %d반) · 평균 %.1f점",
                        s.getStudentName(), s.getGrade(), s.getClassNum(), s.getOverallAverage()))
                .orElse(null);
    }

    @SuppressWarnings("unchecked")
    private String callClaude(String systemPrompt, String userMessage) {
        if (apiKey == null || apiKey.isBlank() || apiKey.equals("your-openai-api-key-here")) {
            log.warn("[Chat] API 키가 설정되지 않았습니다.");
            return "[AI 챗봇 미설정] API 키를 환경변수 OPENAI_API_KEY에 설정해주세요.";
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            Map<String, Object> body = Map.of(
                    "model", MODEL,
                    "max_tokens", 1024,
                    "system", systemPrompt,
                    "messages", List.of(
                            Map.of("role", "user", "content", userMessage)
                    )
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(ANTHROPIC_URL, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                // Anthropic 응답 형식: {"content": [{"type": "text", "text": "..."}]}
                List<Map<String, Object>> content = (List<Map<String, Object>>) response.getBody().get("content");
                if (content != null && !content.isEmpty()) {
                    return (String) content.get(0).get("text");
                }
            }
            return "AI 응답을 가져오는 데 실패했습니다.";
        } catch (Exception e) {
            log.error("[Chat] Claude 호출 실패: {}", e.getMessage());
            return "AI 서비스에 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }
    }
}
