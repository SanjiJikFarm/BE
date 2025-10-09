package com.example.SanjiBE.controller;

import com.example.SanjiBE.service.CarbonMonthlyService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carbon")
@Tag(name = "Carbon", description = "탄소 절감량 계산")
@RequiredArgsConstructor
public class CarbonController {

    private final CarbonMonthlyService monthlyService;

    @GetMapping("/monthly/{userId}")
    @Operation(summary = "달 별 탄소 절감량 계산")
    public Map<String, Object> getMonthlySummary(
            @PathVariable Long userId,
            @RequestParam String month
    ) {
        return monthlyService.getMonthlySummary(userId, month);
    }

    @GetMapping("/product/{userId}")
    @Operation(summary = "상품별 탄소 절감량 계산")
    public List<Map<String, Object>> getMonthlyDetail(
            @PathVariable Long userId,
            @RequestParam String month
    ) {
        return monthlyService.getMonthlyDetail(userId, month);
    }

    @GetMapping("/weekly/{userId}")
    @Operation(summary = "주별 탄소 절감량 계산")
    public List<Map<String, Object>> getWeeklySummary(
            @PathVariable Long userId,
            @RequestParam String month
    ) {
        return monthlyService.getWeeklySummary(userId, month);
    }
}
