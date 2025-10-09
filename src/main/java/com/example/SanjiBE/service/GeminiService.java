package com.example.SanjiBE.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    private final RestTemplate restTemplate;

    @Value("${app.gemini.url}")
    private String geminiUrl;

    @Value("${GEMINI_API_KEY}")
    private String apiKey;

    private final Map<String, Double> distanceCache = new ConcurrentHashMap<>();

    public double askDistanceKm(String from, String to) {
        String key = from + "→" + to;
        if (distanceCache.containsKey(key)) return distanceCache.get(key);

        String prompt = String.format("%s와 %s 사이의 실제 도로 이동 거리를 km 단위 숫자로만 알려줘.", from, to);

        try {
            String body = """
                    {
                      "contents": [
                        {
                          "parts": [
                            { "text": "%s" }
                          ]
                        }
                      ]
                    }
                    """.formatted(prompt);

            HttpHeaders headers = new HttpHeaders();
            headers.set("x-goog-api-key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    geminiUrl,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            String text = extractText(response.getBody());
            double km = parseKmValue(text);

            distanceCache.put(key, km);
            return km;
        } catch (Exception e) {
            log.warn("Gemini 호출 실패 ({}): {}", key, e.getMessage());
            return 100.0;
        }
    }

    private double parseKmValue(String text) {
        if (text == null) return 100.0;
        text = text.replaceAll("[^0-9.]", " ").trim();
        for (String part : text.split("\\s+")) {
            if (part.matches("\\d+(\\.\\d+)?")) {
                return Double.parseDouble(part);
            }
        }
        return 100.0;
    }

    private String extractText(Map<String, Object> responseBody) {
        if (responseBody == null) return "";

        Object candidatesObj = responseBody.get("candidates");
        if (!(candidatesObj instanceof Iterable<?> candidates)) return "";

        for (Object c : candidates) {
            if (c instanceof Map<?, ?> candidate) {
                Object contentObj = candidate.get("content");
                if (contentObj instanceof Map<?, ?> content) {
                    Object partsObj = content.get("parts");
                    if (partsObj instanceof Iterable<?> parts) {
                        for (Object p : parts) {
                            if (p instanceof Map<?, ?> part) {
                                Object text = part.get("text");
                                if (text != null) return text.toString();
                            }
                        }
                    }
                }
            }
        }
        return "";
    }

    public String askMainOrigin(String product) {
        String prompt = String.format("%s의 주요 생산지(국가나 지역)를 짧게 알려줘.", product);
        try {
            String body = """
                    {
                      "contents": [
                        {
                          "parts": [
                            { "text": "%s" }
                          ]
                        }
                      ]
                    }
                    """.formatted(prompt);

            HttpHeaders headers = new HttpHeaders();
            headers.set("x-goog-api-key", apiKey);
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(body, headers);

            ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                    geminiUrl,
                    HttpMethod.POST,
                    entity,
                    new ParameterizedTypeReference<>() {}
            );

            return extractText(response.getBody());
        } catch (Exception e) {
            log.warn("Gemini 주요 생산지 추론 실패: {}", e.getMessage());
            return "서울특별시";
        }
    }
}
