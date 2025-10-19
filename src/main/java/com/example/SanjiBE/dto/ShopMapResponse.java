package com.example.SanjiBE.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ShopMapResponse {
    private Long id;
    private String shopName;
    private String shopImage;
    private String address;
    private Double avgRating;
    private Long reviewCount;
    private Double latitude;
    private Double longitude;
    private Double distanceKm;

    public ShopMapResponse(Long id, String shopName, String shopImage, String address,
                           Double avgRating, Long reviewCount,
                           Double latitude, Double longitude) {
        this.id = id;
        this.shopName = shopName;
        this.shopImage = shopImage;
        this.address = address;
        this.avgRating = avgRating;
        this.reviewCount = reviewCount;
        this.latitude = latitude;
        this.longitude = longitude;
        this.distanceKm = null;
    }
}
