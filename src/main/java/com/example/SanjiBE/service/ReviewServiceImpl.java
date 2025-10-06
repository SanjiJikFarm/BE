// src/main/java/com/example/SanjiBE/service/ReviewServiceImpl.java
package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.ReviewResponse;
import com.example.SanjiBE.repository.ReviewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;

    @Override
    public List<ReviewResponse> getMyReviews(Long userId) {
        return reviewRepository.findMyReviews(userId);
    }
}
