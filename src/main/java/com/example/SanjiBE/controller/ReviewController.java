package com.example.SanjiBE.controller;

import com.example.SanjiBE.dto.ReviewRequest;
import com.example.SanjiBE.dto.ReviewResponse;
import com.example.SanjiBE.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/reviews")
@Tag(name = "Review", description = "리뷰 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "나의 리뷰 조회", description = "내가 구매한 상품에 작성한 리뷰 조회")
    @GetMapping
    public List<ReviewResponse> getMyReviews(
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        return reviewService.getMyReviews(userId);
    }

    @PostMapping
    public ResponseEntity<ReviewResponse> createReview(
            @AuthenticationPrincipal(expression = "id") Long userId,
            @Valid @RequestBody ReviewRequest req
    ) {
        ReviewResponse resp = reviewService.create(userId, req);
        return ResponseEntity.ok(resp);
    }
}
