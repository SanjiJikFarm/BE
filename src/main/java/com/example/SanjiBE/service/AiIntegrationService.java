package com.example.SanjiBE.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
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
     * 달별 탄소 절감량 요청
     */
    public Map<String, Object> getMonthlySummary(Long userId, String month) {
        String url = aiServiceUrl + "/api/carbon/monthly?userId=" + userId + "&month=" + month;
        ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
        return response.getBody();
    }

    /**
     * 상품별 탄소 절감량 요청
     */
    public List<Map<String, Object>> getProductDetail(Long userId, String month) {
        String url = aiServiceUrl + "/api/carbon/product?userId=" + userId + "&month=" + month;
        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
        return response.getBody();
    }

    /**
     * 주별 탄소 절감량 요청
     */
    public List<Map<String, Object>> getWeeklySummary(Long userId, String month) {
        String url = aiServiceUrl + "/api/carbon/weekly?userId=" + userId + "&month=" + month;
        ResponseEntity<List> response = restTemplate.getForEntity(url, List.class);
        return response.getBody();
    }
}
