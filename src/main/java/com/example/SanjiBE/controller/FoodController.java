package com.example.SanjiBE.controller;

import java.util.List;

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
public class FoodController {
  
  private final FoodService foodService;

  public FoodController(FoodService foodService){
    this.foodService = foodService;
  }

  // 이번달 식재료 조회
  @GetMapping("/month")
    public ResponseEntity<List<FoodApiResponse>> getCurrentMonth() {
        return ResponseEntity.ok(foodService.getCurrentMonthFood());
    }

  // 특정 월 식재료 조회
  @GetMapping("/month/{month}")
  public ResponseEntity<List<FoodApiResponse>> getByMonth(@PathVariable int month) {
      if (month < 1 || month > 12) {
          return ResponseEntity.badRequest().build();
      }
      return ResponseEntity.ok(foodService.getFoodForMonth(month));
  }

}
