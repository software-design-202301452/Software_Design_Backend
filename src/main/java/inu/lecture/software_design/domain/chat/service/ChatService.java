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
 * 분석 DB의 학생 학습 요약 데이터를 시스템 프롬프트에 주입하여 GPT와 대화한다.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final StudentLearningSummaryRepository summaryRepository;
    private final RestTemplate restTemplate;

    @Value("${openai.api-key:}")
    private String openAiApiKey;

    private static final String OPENAI_URL = "https://factchat-cloud.mindlogic.ai/v1/api/openai/chat/completions";
    private static final String SYSTEM_PROMPT_BASE =
            "당신은 학생 관리 시스템의 AI 보조 교사입니다. " +
            "교사가 학생의 학습 현황, 성적, 피드백, 상담 내역에 대해 질문하면 분석된 데이터를 바탕으로 답변하세요. " +
            "답변은 항상 한국어로 하며, 구체적이고 도움이 되는 내용을 제공하세요.";

    public ChatResponse chat(ChatRequest request) {
        String systemPrompt = buildSystemPrompt(request.getStudentId());
        String contextDescription = buildContextDescription(request.getStudentId());

        String reply = callOpenAi(systemPrompt, request.getMessage());

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
    private String callOpenAi(String systemPrompt, String userMessage) {
        if (openAiApiKey == null || openAiApiKey.isBlank() || openAiApiKey.equals("your-openai-api-key-here")) {
            log.warn("[Chat] AI:NU API 키가 설정되지 않았습니다. 더미 응답을 반환합니다.");
            return "[AI 챗봇 미설정] API 키를 환경변수 OPENAI_API_KEY에 설정해주세요. " +
                   "현재 질문: \"" + userMessage + "\"";
        }

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(openAiApiKey);

            Map<String, Object> body = Map.of(
                    "model", "gpt-5-chat-latest",
                    "messages", List.of(
                            Map.of("role", "system", "content", systemPrompt),
                            Map.of("role", "user", "content", userMessage)
                    ),
                    "max_tokens", 1000,
                    "temperature", 0.7
            );

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
            ResponseEntity<Map> response = restTemplate.postForEntity(OPENAI_URL, entity, Map.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                List<Map<String, Object>> choices = (List<Map<String, Object>>) response.getBody().get("choices");
                if (choices != null && !choices.isEmpty()) {
                    Map<String, Object> message = (Map<String, Object>) choices.get(0).get("message");
                    return (String) message.get("content");
                }
            }
            return "AI 응답을 가져오는 데 실패했습니다.";
        } catch (Exception e) {
            log.error("[Chat] OpenAI 호출 실패: {}", e.getMessage());
            return "AI 서비스에 일시적인 오류가 발생했습니다. 잠시 후 다시 시도해주세요.";
        }
    }
}
