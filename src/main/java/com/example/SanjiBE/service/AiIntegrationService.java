package com.example.SanjiBE.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Map;

@Service
public class AiIntegrationService {

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${AI_SERVICE_URL:http://localhost:8081}")
    private String aiServiceUrl;

    /**
     * 달별 탄소 절감량 요청 (POST)
     */
    public Map<String, Object> getMonthlySummary(Long userId, String month) {
        String url = aiServiceUrl + "/api/carbon/monthly";

        Map<String, Object> requestBody = Map.of(
                "userId", userId,
                "month", month,
                "receipts", List.of() // 아직 receipt 데이터 없으면 빈 리스트로 전달
        );

        return postRequest(url, requestBody, Map.class);
    }

    /**
     * 상품별 탄소 절감량 요청 (POST)
     */
    public List<Map<String, Object>> getProductDetail(Long userId, String month) {
        String url = aiServiceUrl + "/api/carbon/product";

        Map<String, Object> requestBody = Map.of(
                "userId", userId,
                "month", month,
                "receipts", List.of()
        );

        return postRequest(url, requestBody, List.class);
    }

    /**
     * 주별 탄소 절감량 요청 (POST)
     */
    public List<Map<String, Object>> getWeeklySummary(Long userId, String month) {
        String url = aiServiceUrl + "/api/carbon/weekly";

        Map<String, Object> requestBody = Map.of(
                "userId", userId,
                "month", month,
                "receipts", List.of()
        );

        return postRequest(url, requestBody, List.class);
    }

    /**
     * 공통 POST 요청 처리 메서드
     */
    private <T> T postRequest(String url, Map<String, Object> body, Class<T> responseType) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        ResponseEntity<T> response = restTemplate.postForEntity(url, entity, responseType);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("AI 서버 응답 실패: " + response.getStatusCode());
        }

        return response.getBody();
    }
}
