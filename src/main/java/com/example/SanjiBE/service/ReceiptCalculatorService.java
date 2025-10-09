package com.example.SanjiBE.service;

import com.example.SanjiBE.entity.ImportOrigin;
import com.example.SanjiBE.entity.LocalOrigin;
import com.example.SanjiBE.repository.ImportOriginRepository;
import com.example.SanjiBE.repository.LocalOriginRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class ReceiptCalculatorService {

    private final LocalOriginRepository localRepo;
    private final ImportOriginRepository importRepo;
    private final OpenAiService openai;
    private final CarbonService carbon;

    private final Map<String, Double> distanceCache = new ConcurrentHashMap<>();

    public double calculateSingle(String storeName, String productName, double weightKg) {
        double saved = 0.0;

        // 국내산 원산지 확인
        Optional<LocalOrigin> local = localRepo.findFirstByItemNameContaining(productName);
        if (local.isPresent()) {
            String origin = local.get().getRegion();
            double dist = askDistance(origin, storeName);
            double cLocal = carbon.truck(dist, weightKg);
            double cRef = carbon.referenceFoodMileage(weightKg);
            saved = Math.max(cRef - cLocal, 0.0);
            return roundTo3(saved);
        }

        // 수입산 확인
        Optional<ImportOrigin> imp = importRepo.findFirstByItemNameContaining(productName);
        if (imp.isPresent()) {
            String country = imp.get().getCountry();
            double sea = askDistance(country + " 주요 항구", "부산항");
            double inland = askDistance("부산항", storeName);
            double cImport = carbon.ship(sea, weightKg) + carbon.truck(inland, weightKg);
            double cRef = carbon.referenceFoodMileage(weightKg);
            saved = Math.max(cRef - cImport, 0.0);
            return roundTo3(saved);
        }

        // 둘 다 없으면 GPT로 주요 산지 추정
        String predicted = openai.ask(
                String.format("%s의 주요 생산지를 한 문장으로 알려줘. 지역명만 말해.", productName)
        );
        double dist = askDistance(predicted, storeName);
        double cLocal = carbon.truck(dist, weightKg);
        double cRef = carbon.referenceFoodMileage(weightKg);
        saved = Math.max(cRef - cLocal, 0.0);
        return roundTo3(saved);
    }

    private double askDistance(String origin, String destination) {
        String key = origin + "->" + destination;

        if (distanceCache.containsKey(key)) {
            return distanceCache.get(key);
        }

        String prompt = String.format("%s와 %s 사이의 실제 도로 거리를 킬로미터 단위의 숫자만으로 알려줘.", origin, destination);
        String response = openai.ask(prompt);
        double result;
        try {
            result = Double.parseDouble(response.replaceAll("[^0-9.]", ""));
        } catch (NumberFormatException e) {
            result = 100.0;
        }

        distanceCache.put(key, result);
        return result;
    }

    private double roundTo3(double value) {
        return Math.round(value * 1000.0) / 1000.0;
    }
}
