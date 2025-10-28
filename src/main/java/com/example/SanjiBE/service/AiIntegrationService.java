package com.example.SanjiBE.service;

import com.example.SanjiBE.entity.ImportOrigin;
import com.example.SanjiBE.entity.LocalOrigin;
import com.example.SanjiBE.entity.Receipt;
import com.example.SanjiBE.repository.ImportOriginRepository;
import com.example.SanjiBE.repository.LocalOriginRepository;
import com.example.SanjiBE.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class AiIntegrationService {

    private final ReceiptRepository receiptRepository;
    private final ImportOriginRepository importRepo;
    private final LocalOriginRepository localRepo;
    private final CarbonService carbonService;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${AI_SERVICE_URL:http://localhost:8081}")
    private String aiServiceUrl;

    public List<Map<String, Object>> getProductDetail(Long userId, String month) {
        int year = java.time.LocalDate.now().getYear();
        int monthNum = Integer.parseInt(month);
        List<Receipt> receipts = receiptRepository.findWithPurchasesByUserAndYearMonth(userId, year, monthNum);

        List<Map<String, Object>> results = new ArrayList<>();

        for (Receipt r : receipts) {
            for (var p : r.getPurchases()) {
                String product = p.getProduct().getProductName();
                String store = r.getShop().getShopName();
                double qty = p.getQuantity();
                double weight = qty * 0.2;

                // DB 기반 산지/수입국 확인
                Optional<LocalOrigin> local = localRepo.findByItemNameContaining(product);
                Optional<ImportOrigin> imp = importRepo.findByItemNameContaining(product);

                double savedKg;
                if (local.isPresent()) {
                    savedKg = carbonService.referenceFoodMileage(weight) - carbonService.truck(200, weight);
                } else if (imp.isPresent()) {
                    savedKg = carbonService.referenceFoodMileage(weight) - carbonService.ship(4000, weight);
                } else {
                    savedKg = callAiServerForSingle(store, product, weight);
                }

                savedKg = Math.max(savedKg, 0.0);

                Map<String, Object> result = new HashMap<>();
                result.put("store", store);
                result.put("product", product);
                result.put("quantity", qty);
                result.put("savedKg", Math.round(savedKg * 1000.0) / 1000.0);
                result.put("date", r.getReceiptDate().toString());
                results.add(result);
            }
        }

        return results;
    }

    public Map<String, Object> getMonthlySummary(Long userId, String month) {
        List<Map<String, Object>> productDetails = getProductDetail(userId, month);

        double totalSaved = productDetails.stream()
                .mapToDouble(p -> ((Number) p.get("savedKg")).doubleValue())
                .sum();

        int count = productDetails.size();

        Map<String, Object> summary = new HashMap<>();
        summary.put("userId", userId);
        summary.put("month", month);
        summary.put("purchaseCount", count);
        summary.put("totalSavedKg", Math.round(totalSaved * 1000.0) / 1000.0);

        return summary;
    }

    public List<Map<String, Object>> getWeeklySummary(Long userId, String month) {
        List<Map<String, Object>> productDetails = getProductDetail(userId, month);

        Map<Integer, Double> weeklyTotals = new HashMap<>();
        for (Map<String, Object> p : productDetails) {
            String date = (String) p.get("date");
            int day = java.time.LocalDate.parse(date).getDayOfMonth();
            int week = (day - 1) / 7 + 1;
            double saved = ((Number) p.get("savedKg")).doubleValue();
            weeklyTotals.put(week, weeklyTotals.getOrDefault(week, 0.0) + saved);
        }

        List<Map<String, Object>> result = new ArrayList<>();
        for (int week = 1; week <= 4; week++) {
            Map<String, Object> data = new HashMap<>();
            data.put("week", week);
            data.put("savedKg", Math.round(weeklyTotals.getOrDefault(week, 0.0) * 1000.0) / 1000.0);
            result.add(data);
        }

        return result;
    }

    private double callAiServerForSingle(String store, String product, double weightKg) {
        String url = aiServiceUrl + "/api/carbon/product";

        Map<String, Object> body = new HashMap<>();
        body.put("userId", 0);
        body.put("month", "2025-10");

        Map<String, Object> purchase = new HashMap<>();
        purchase.put("productName", product);
        purchase.put("quantity", weightKg / 0.2);

        Map<String, Object> receipt = new HashMap<>();
        receipt.put("storeName", store);
        receipt.put("purchases", List.of(purchase));

        body.put("receipts", List.of(receipt));

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

            ResponseEntity<List> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    entity,
                    List.class
            );

            if (response.getStatusCode().is2xxSuccessful() &&
                    response.getBody() != null &&
                    !response.getBody().isEmpty()) {

                Object first = response.getBody().get(0);
                if (first instanceof Map<?, ?> map) {
                    Object savedKg = map.get("savedKg");
                    return savedKg != null ? Double.parseDouble(savedKg.toString()) : 0.0;
                }
            }
        } catch (Exception e) {
            System.err.println("AI 서버 호출 실패: " + e.getMessage());
        }

        return 0.0;
    }
}
