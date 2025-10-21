package com.example.SanjiBE.ai.service;

import com.example.SanjiBE.entity.Purchase;
import com.example.SanjiBE.entity.Receipt;
import com.example.SanjiBE.repository.ReceiptRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CarbonMonthlyService {

    private final ReceiptRepository receiptRepository;
    private final ReceiptCalculatorService calculator;

    private static final ExecutorService executor = Executors.newFixedThreadPool(10);

    public Map<String, Object> getMonthlySummary(Long userId, String month) {
        int monthValue = Integer.parseInt(month);
        int year = LocalDate.now().getYear();

        List<Receipt> receipts = receiptRepository.findWithPurchasesByUserAndYearMonth(userId, year, monthValue);

        List<CompletableFuture<Double>> futures = receipts.stream()
                .flatMap(receipt -> receipt.getPurchases().stream()
                        .map(purchase -> CompletableFuture.supplyAsync(() -> {
                            String storeName = receipt.getShop().getShopName();
                            String productName = purchase.getProduct().getProductName();
                            double weightKg = purchase.getQuantity() * 0.2;
                            return calculator.calculateSingle(storeName, productName, weightKg);
                        }, executor))
                )
                .collect(Collectors.toList());

        double totalSaved = futures.stream()
                .mapToDouble(CompletableFuture::join)
                .sum();

        int purchaseCount = futures.size();

        Map<String, Object> summary = new HashMap<>();
        summary.put("userId", userId);
        summary.put("month", month);
        summary.put("purchaseCount", purchaseCount);
        summary.put("totalSavedKg", roundTo3(totalSaved));
        return summary;
    }

    public List<Map<String, Object>> getMonthlyDetail(Long userId, String month) {
        int monthValue = Integer.parseInt(month);
        int year = LocalDate.now().getYear();

        List<Receipt> receipts = receiptRepository.findWithPurchasesByUserAndYearMonth(userId, year, monthValue);

        List<CompletableFuture<Map<String, Object>>> futures = receipts.stream()
                .flatMap(receipt -> receipt.getPurchases().stream()
                        .map(purchase -> CompletableFuture.supplyAsync(() -> {
                            String storeName = receipt.getShop().getShopName();
                            String productName = purchase.getProduct().getProductName();
                            double weightKg = purchase.getQuantity() * 0.2;
                            double saved = calculator.calculateSingle(storeName, productName, weightKg);

                            Map<String, Object> detail = new HashMap<>();
                            detail.put("store", storeName);
                            detail.put("product", productName);
                            detail.put("quantity", purchase.getQuantity());
                            detail.put("savedKg", roundTo3(saved));
                            detail.put("date", receipt.getReceiptDate().toString());
                            return detail;
                        }, executor))
                )
                .collect(Collectors.toList());

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }

    public List<Map<String, Object>> getWeeklySummary(Long userId, String month) {
        int monthValue = Integer.parseInt(month);
        int year = LocalDate.now().getYear();

        List<Receipt> receipts = receiptRepository.findWithPurchasesByUserAndYearMonth(userId, year, monthValue);

        // 주차별 합계를 저장할 맵
        Map<Integer, Double> weekTotals = new HashMap<>();

        for (Receipt receipt : receipts) {
            LocalDate date = receipt.getReceiptDate();
            int day = date.getDayOfMonth();

            int weekNum;
            if (day <= 7) weekNum = 1;
            else if (day <= 14) weekNum = 2;
            else if (day <= 21) weekNum = 3;
            else weekNum = 4;

            double totalForReceipt = 0.0;
            for (Purchase purchase : receipt.getPurchases()) {
                String storeName = receipt.getShop().getShopName();
                String productName = purchase.getProduct().getProductName();
                double weightKg = purchase.getQuantity() * 0.2;
                totalForReceipt += calculator.calculateSingle(storeName, productName, weightKg);
            }

            weekTotals.put(weekNum, weekTotals.getOrDefault(weekNum, 0.0) + totalForReceipt);
        }

        // 주차별 결과 리스트로 변환
        List<Map<String, Object>> result = new ArrayList<>();
        for (int week = 1; week <= 4; week++) {
            Map<String, Object> data = new HashMap<>();
            data.put("week", week);
            data.put("savedKg", roundTo3(weekTotals.getOrDefault(week, 0.0)));
            result.add(data);
        }

        return result;
    }

    private double roundTo3(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}
