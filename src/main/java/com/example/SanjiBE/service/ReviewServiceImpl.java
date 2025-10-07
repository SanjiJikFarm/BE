// src/main/java/com/example/SanjiBE/service/ReviewServiceImpl.java
package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.ReviewRequest;
import com.example.SanjiBE.dto.ReviewResponse;
import com.example.SanjiBE.entity.Product;
import com.example.SanjiBE.entity.Review;
import com.example.SanjiBE.entity.User;
import com.example.SanjiBE.repository.*;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;


    @Override
    public List<ReviewResponse> getMyReviews(Long userId) {
        return reviewRepository.findMyReviews(userId);
    }

    @Transactional
    @Override
    public ReviewResponse create(Long userId, ReviewRequest req) {
        // 1) 사용자 조회. 없으면 404 성격의 예외를 던진다.
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));

        // 2) 상품 조회. 없으면 404 성격의 예외를 던진다.
        Product product = productRepository.findById(req.getProductId())
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        // 3) 구매 여부 검증. 정책: 구매한 경우에만 리뷰 가능.
        boolean purchased =
                purchaseRepository.existsByOrder_User_IdAndProduct_Id(userId, product.getId());

        // 4) 중복 리뷰 방지. 정책: 사용자×상품 1건만 허용.
        if (!purchased) {
            throw new IllegalStateException("구매한 상품에만 리뷰를 등록할 수 있습니다.");
        }

        if (reviewRepository.existsByUser_IdAndProduct_Id(userId, product.getId())) {
            throw new IllegalStateException("이미 해당 상품에 대해 리뷰를 등록했습니다.");
        }

        // 5) 엔티티 생성 및 저장. createdAt, reviewLike 초기화는 @PrePersist
        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setReviewField(req.getContent());
        review.setRating(req.getRating());
        review.setReviewPhotoUrl(req.getPhotoUrl());

        Review saved = reviewRepository.save(review);

        // 6) 응답 DTO 매핑
        ReviewResponse resp = new ReviewResponse();
        resp.setReviewId(saved.getId());
        resp.setProductId(product.getId());
        resp.setProductName(product.getProductName());
        resp.setCreatedAt(saved.getCreatedAt());
        resp.setRating(saved.getRating());
        resp.setImageUrl(saved.getReviewPhotoUrl());
        resp.setText(saved.getReviewField());
        resp.setProductLike(product.getProductLike());
        resp.setLiked(false);
        return resp;
    }


}
