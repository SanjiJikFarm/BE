package com.example.SanjiBE.ai.controller;

import com.example.SanjiBE.ai.service.CarbonMonthlyService;
import com.example.SanjiBE.security.CustomUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/carbon")
@Tag(name = "Carbon", description = "탄소 절감량 계산")
@RequiredArgsConstructor
public class CarbonController {

    private final CarbonMonthlyService monthlyService;

    @GetMapping("/monthly")
    @Operation(summary = "달 별 탄소 절감량 계산")
    public Map<String, Object> getMonthlySummary(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam String month
    ) {
        return monthlyService.getMonthlySummary(user.getId(), month);
    }

    @GetMapping("/product")
    @Operation(summary = "상품별 탄소 절감량 계산")
    public List<Map<String, Object>> getMonthlyDetail(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam String month
    ) {
        return monthlyService.getMonthlyDetail(user.getId(), month);
    }

    @GetMapping("/weekly")
    @Operation(summary = "주별 탄소 절감량 계산")
    public List<Map<String, Object>> getWeeklySummary(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam String month
    ) {
        return monthlyService.getWeeklySummary(user.getId(), month);
    }
}
