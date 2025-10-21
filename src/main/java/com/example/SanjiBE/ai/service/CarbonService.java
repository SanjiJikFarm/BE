package com.example.SanjiBE.ai.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CarbonService {

    @Value("${app.carbon.truckKgPerTonKm:0.18}")
    private double truckKgPerTonKm;

    @Value("${app.carbon.shipKgPerTonKm:0.012}")
    private double shipKgPerTonKm;

    @Value("${app.carbon.referenceKm:400}")
    private double referenceKm;

    public double truck(double distanceKm, double weightKg) {
        if (distanceKm <= 0 || weightKg <= 0) return 0.0;
        double ton = weightKg / 1000.0;
        return distanceKm * ton * truckKgPerTonKm;
    }

    public double ship(double distanceKm, double weightKg) {
        if (distanceKm <= 0 || weightKg <= 0) return 0.0;
        double ton = weightKg / 1000.0;
        return distanceKm * ton * shipKgPerTonKm;
    }

    public double referenceFoodMileage(double weightKg) {
        return truck(referenceKm, weightKg);
    }
}
