// 코드 제목: ShopResponse DTO - JPQL 프로젝션 타입 맞춤 생성자 추가
package com.example.SanjiBE.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ShopResponse {
    private Long id;
    private String shopName;
    private String shopImage;
    private String address;
    private Double avgRating; // AVG -> Double
    private Long reviewCount; // COUNT -> Long

    public ShopResponse(Long id, String shopName, String shopImage, String address,
                        Double avgRating, Long reviewCount) {
        this.id = id;
        this.shopName = shopName;
        this.shopImage = shopImage;
        this.address = address;
        this.avgRating = avgRating != null ? avgRating : 0.0;
        this.reviewCount = reviewCount != null ? reviewCount : 0L;
    }

}
