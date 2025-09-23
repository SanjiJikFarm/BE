package com.example.SanjiBE.dto;


import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductResponse {
    private Long productId;
    private String productName;
    private String productImage;
    private int productPrice;
    private long reviewCount;
    private double averageRating;
    private int productLike;
    private boolean isLiked;
}
