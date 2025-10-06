package com.example.SanjiBE.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ReviewResponse {
    private Long reviewId;
    private Long productId;
    private String productName;
    private LocalDateTime createdAt; // 날짜
    private int rating;              // 별점
    private String imageUrl;         // 이미지
    private String text;             // 텍스트
    private Integer productLike;       // 찜 개수
    private boolean isLiked;
}
