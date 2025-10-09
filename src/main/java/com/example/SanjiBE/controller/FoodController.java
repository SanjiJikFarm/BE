package com.example.SanjiBE.controller;

import java.util.List;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.SanjiBE.dto.FoodApiResponse;
import com.example.SanjiBE.service.FoodService;

@RestController
@RequestMapping("api/food")
@CrossOrigin(origins = "*") // CORS 설정 (개발용)
@Tag(name = "Food", description = "식재료 추천")
public class FoodController {
  
  private final FoodService foodService;

  public FoodController(FoodService foodService){
    this.foodService = foodService;
  }

  // 이번 달 식재료 조회
  @GetMapping("/month")
  @Operation(summary = "이번 달 식재료 조회")
    public ResponseEntity<List<FoodApiResponse>> getCurrentMonth() {
        return ResponseEntity.ok(foodService.getCurrentMonthFood());
    }

  // 특정 월 식재료 조회
  @GetMapping("/month/{month}")
  @Operation(summary = "특정 월 식재료 조회")
  public ResponseEntity<List<FoodApiResponse>> getByMonth(@PathVariable int month) {
      if (month < 1 || month > 12) {
          return ResponseEntity.badRequest().build();
      }
      return ResponseEntity.ok(foodService.getFoodForMonth(month));
  }

}
