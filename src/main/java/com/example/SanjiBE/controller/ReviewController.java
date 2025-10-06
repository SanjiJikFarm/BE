package com.example.SanjiBE.controller;

import com.example.SanjiBE.dto.ReviewResponse;
import com.example.SanjiBE.service.ReviewService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/users/reviews")
@Tag(name = "Review", description = "리뷰 관련 API")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "나의 리뷰 조회", description = "내가 구매한 상품에 작성한 리뷰 조회")
    @GetMapping
    public List<ReviewResponse> getMyReviews(@PathVariable Long userId) {
        return reviewService.getMyReviews(userId);
    }
}
