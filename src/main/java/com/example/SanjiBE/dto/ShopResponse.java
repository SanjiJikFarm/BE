package com.example.SanjiBE.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ShopResponse {
    private Long shopId;
    private String shopName;
    private String shopImage;
    private String address;
    private Double averageRating;
}

