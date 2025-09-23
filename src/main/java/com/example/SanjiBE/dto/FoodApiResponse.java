package com.example.SanjiBE.dto;

public class FoodApiResponse {
  private String foodNum; // 식재료 번호(cntntsNo)
  private String foodName; // 식재료 이름(fdmtNm)
  private String imageUrl; // 이미지 URL(경로 + 파일명)

  // 기본 생성자
  public FoodApiResponse() {}

  public FoodApiResponse(String foodNum, String foodName, String imageUrl){
    this.foodNum = foodNum;
    this.foodName = foodName;
    this.imageUrl = imageUrl;
  }

  // Getter
  public String getFoodNum(){
    return foodNum;
  }

  public String getFoodName(){
    return foodName;
  }

  public String getImageUrl(){
    return imageUrl;
  }

  //Setter
  public void setFoodNum(String foodNum){
    this.foodNum = foodNum;
  }

  public void setFoodName(String foodName){
    this.foodNum = foodName;
  }

  public void getImageUrl(String imageUrl){
    this.imageUrl = imageUrl;
  }
}


