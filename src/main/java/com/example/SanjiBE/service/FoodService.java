package com.example.SanjiBE.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.example.SanjiBE.dto.FoodApiResponse;
import com.example.SanjiBE.external.NongsaroClient;

@Service
public class FoodService {
  
  private final NongsaroClient client;

  public FoodService(NongsaroClient client){
    this.client = client;
  }

  public List<FoodApiResponse> getCurrentMonthFood(){
    int month = LocalDate.now(ZoneId.of("Asia/Seoul")).getMonthValue();
    return getFoodForMonth(month);
  }

  public List<FoodApiResponse> getFoodForMonth(int month){
    int [] years = {2019, 2018, 2017, 2016};

    Map<String, FoodApiResponse> dedup = new LinkedHashMap<>();

    for(int y : years){
      List<FoodApiResponse> oneYear = client.fetchMonthFood(y, month);
      for (FoodApiResponse f : oneYear) {
        dedup.putIfAbsent(f.getFoodNum(), f);
      }
    }

    return new ArrayList<>(dedup.values());
  }
}
