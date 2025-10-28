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
                "month", month
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(url, entity, Map.class);

        return response.getBody();
    }

    /**
     * 상품별 탄소 절감량 요청 (POST)
     */
    public List<Map<String, Object>> getProductDetail(Long userId, String month) {
        String url = aiServiceUrl + "/api/carbon/product";

        Map<String, Object> requestBody = Map.of(
                "userId", userId,
                "month", month
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<List> response = restTemplate.postForEntity(url, entity, List.class);

        return response.getBody();
    }

    /**
     * 주별 탄소 절감량 요청 (POST)
     */
    public List<Map<String, Object>> getWeeklySummary(Long userId, String month) {
        String url = aiServiceUrl + "/api/carbon/weekly";

        Map<String, Object> requestBody = Map.of(
                "userId", userId,
                "month", month
        );

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(requestBody, headers);
        ResponseEntity<List> response = restTemplate.postForEntity(url, entity, List.class);

        return response.getBody();
    }
}
