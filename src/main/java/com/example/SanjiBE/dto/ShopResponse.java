package com.example.SanjiBE.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor

public class ShopResponse {
    private Long id;
    private String shopName;
    private String shopImage;
    private String address;
    private Double avgRating;
    private Long reviewCount;


    public ShopResponse(Long id, String shopName, String shopImage, String address, Double avgRating, Long reviewCount) {
        this.id = id;
        this.shopName = shopName;
        this.shopImage = shopImage;
        this.address = address;
        this.avgRating = avgRating;
        this.reviewCount = reviewCount;
    }
}
