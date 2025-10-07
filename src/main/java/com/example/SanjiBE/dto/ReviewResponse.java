package com.example.SanjiBE.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

/**
 * 리뷰 응답 DTO: 상품이름, 날짜, 별점, 이미지, 텍스트, 찜(개수, isLiked)
 */
@Getter
@Setter
@NoArgsConstructor      // 기본 생성자 Lombok으로 제공
@AllArgsConstructor
public class ReviewResponse {
    private Long reviewId;
    private Long productId;
    private String productName;
    private LocalDateTime createdAt; // 날짜
    private int rating;              // 별점
    private String imageUrl;         // 이미지
    private String text;             // 텍스트
    private Integer productLike;     // 찜 개수(상품)
    private boolean isLiked;         // 사용자 찜 여부
}
