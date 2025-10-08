package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.ReviewRequest;
import com.example.SanjiBE.dto.ReviewResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ReviewService {

    // 내가 구매한 상품에 작성한 리뷰 목록 (Page)
    Page<ReviewResponse> getMyReviews(Long userId, Pageable pageable);

    // 리뷰 생성
    ReviewResponse create(Long userId, Long productId, ReviewRequest req);

    // 리뷰 수정
    ReviewResponse update(Long userId, Long reviewId, ReviewRequest req);

    // 리뷰 삭제
    void delete(Long userId, Long reviewId);

    // 매장 리뷰 목록
    Page<ReviewResponse> getShopReviews(Long shopId, Long viewerUserId, Pageable pageable);
}