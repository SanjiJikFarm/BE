package com.example.SanjiBE.controller;

import com.example.SanjiBE.dto.ReviewResponse;
import com.example.SanjiBE.dto.ReviewRequest;
import com.example.SanjiBE.dto.ReviewResponse;
import com.example.SanjiBE.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Review", description = "리뷰 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "나의 리뷰 조회", description = "내가 구매한 상품에 작성한 리뷰 목록")
    @GetMapping("/me/reviews")
    public Page<ReviewResponse> getMyReviews(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @ParameterObject Pageable pageable
    ) {
        return reviewService.getMyReviews(userId, pageable);
    }

    @Operation(summary = "상품에 리뷰 작성", description = "구매자만 작성 가능")
    @PostMapping("/products/{productId}/reviews")
    public ResponseEntity<ReviewResponse> createReview(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long productId,
            @Valid @RequestBody ReviewRequest req
    ) {
        ReviewResponse resp = reviewService.create(userId, productId, req);
        return ResponseEntity.created(URI.create("/api/reviews/" + resp.getReviewId()))
                .body(resp);
    }

    @Operation(summary = "리뷰 수정")
    @PatchMapping("/reviews/{reviewId}")
    public ReviewResponse updateReview(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long reviewId,
            @Valid @RequestBody ReviewRequest req
    ) {
        return reviewService.update(userId, reviewId, req);
    }

    @Operation(summary = "리뷰 삭제")
    @DeleteMapping("/reviews/{reviewId}")
    public ResponseEntity<Void> deleteReview(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @PathVariable Long reviewId
    ) {
        reviewService.delete(userId, reviewId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "매장 리뷰 목록", description = "매장 내 모든 상품의 리뷰(상품단위)를 합쳐서 조회")
    @GetMapping("/shops/{shopId}/reviews")
    public Page<ReviewResponse> getShopReviews(
            @PathVariable Long shopId,
            @AuthenticationPrincipal(expression = "id") Long userId, // 추가: 로그인 사용자 id (nullable)
            @ParameterObject Pageable pageable
    ) {
        return reviewService.getShopReviews(shopId, userId, pageable);
    }
}
