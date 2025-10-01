package com.example.SanjiBE.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
public class FoodApiResponse {
    // Getter
    //Setter
    @Setter
    private String foodNum; // 식재료 번호(cntntsNo)
  private String foodName; // 식재료 이름(fdmtNm)
  private String imageUrl; // 이미지 URL(경로 + 파일명)


  public FoodApiResponse(String foodNum, String foodName, String imageUrl){
    this.foodNum = foodNum;
    this.foodName = foodName;
    this.imageUrl = imageUrl;
  }

}


