package com.example.SanjiBE.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
@Service
public class OpenAiService {

    @Value("${OPENAI_API_URL}")
    private String openAiUrl;

    @Value("${OPENAI_API_KEY}")
    private String apiKey;

    @Value("${OPENAI_MODEL}")
    private String model;

    private final RestTemplate restTemplate = new RestTemplate();

    public String ask(String prompt) {
        try {
            Map<String, Object> request = Map.of(
                    "model", model,
                    "messages", List.of(
                            Map.of("role", "system", "content", "You are a precise assistant that returns only concise numeric or factual answers."),
                            Map.of("role", "user", "content", prompt)
                    ),
                    "max_tokens", 150,
                    "temperature", 0.3
            );

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(apiKey);

            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    openAiUrl,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            Map<String, Object> responseBody = response.getBody();
            if (responseBody == null || !responseBody.containsKey("choices")) {
                log.warn("OpenAI 응답 비정상: {}", responseBody);
                return "0";
            }

            List<Map<String, Object>> choices = (List<Map<String, Object>>) responseBody.get("choices");
            if (choices == null || choices.isEmpty()) {
                log.warn("OpenAI 응답에 choices 없음: {}", responseBody);
                return "0";
            }

            Map<String, Object> message = (Map<String, Object>) Objects.requireNonNull(choices.get(0).get("message"));
            String content = Objects.toString(message.get("content"), "").trim();

            return content.isEmpty() ? "0" : content;

        } catch (Exception e) {
            log.warn("OpenAI 호출 실패: {}", e.getMessage());
            return "0";
        }
    }
}
