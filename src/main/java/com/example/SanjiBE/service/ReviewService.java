package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.ReviewRequest;
import com.example.SanjiBE.dto.ReviewResponse;

import java.util.List;

public interface ReviewService {
    List<ReviewResponse> getMyReviews(Long userId);
    ReviewResponse create(Long userId, ReviewRequest req);
}