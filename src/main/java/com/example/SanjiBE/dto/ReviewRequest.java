package com.example.SanjiBE.dto;

import jakarta.validation.constraints.*;

public class ReviewRequest {

    @NotNull(message = "productId는 필수입니다.")
    private Long productId;

    @NotBlank(message = "리뷰 내용을 입력하세요.")
    private String content;

    @Min(value = 1, message = "별점은 1 이상이어야 합니다.")
    @Max(value = 5, message = "별점은 5 이하여야 합니다.")
    private int rating;

    private String photoUrl;

    public Long getProductId() { return productId; }
    public void setProductId(Long productId) { this.productId = productId; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public int getRating() { return rating; }
    public void setRating(int rating) { this.rating = rating; }

    public String getPhotoUrl() { return photoUrl; }
    public void setPhotoUrl(String photoUrl) { this.photoUrl = photoUrl; }
}
