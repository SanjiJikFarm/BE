package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.ReviewRequest;
import com.example.SanjiBE.dto.ReviewResponse;
import com.example.SanjiBE.entity.Product;
import com.example.SanjiBE.entity.Review;
import com.example.SanjiBE.entity.User;
import com.example.SanjiBE.repository.ProductRepository;
import com.example.SanjiBE.repository.PurchaseRepository;
import com.example.SanjiBE.repository.ReviewRepository;
import com.example.SanjiBE.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ReviewServiceImpl implements ReviewService {

    private final ReviewRepository reviewRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final PurchaseRepository purchaseRepository;

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getMyReviews(Long userId, Pageable pageable) {
        return reviewRepository.findMyReviews(userId, pageable);
    }

    @Override
    @Transactional
    public ReviewResponse create(Long userId, Long productId, ReviewRequest req) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다."));
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        boolean purchased = purchaseRepository
                .existsByOrder_User_IdAndProduct_Id(userId, product.getId());
        if (!purchased) {
            throw new IllegalStateException("구매한 상품에만 리뷰를 등록할 수 있습니다.");
        }
        if (reviewRepository.existsByUser_IdAndProduct_Id(userId, product.getId())) {
            throw new IllegalStateException("이미 해당 상품에 대해 리뷰를 등록했습니다.");
        }

        Review review = new Review();
        review.setUser(user);
        review.setProduct(product);
        review.setReviewField(req.getContent());
        review.setRating(req.getRating());
        review.setReviewPhotoUrl(req.getPhotoUrl());

        Review saved = reviewRepository.save(review);

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

    @Override
    @Transactional
    public ReviewResponse update(Long userId, Long reviewId, ReviewRequest req) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalStateException("본인이 작성한 리뷰만 수정할 수 있습니다.");
        }

        review.setReviewField(req.getContent());
        review.setRating(req.getRating());
        review.setReviewPhotoUrl(req.getPhotoUrl());

        Review saved = reviewRepository.save(review);

        ReviewResponse resp = new ReviewResponse();
        resp.setReviewId(saved.getId());
        resp.setProductId(saved.getProduct().getId());
        resp.setProductName(saved.getProduct().getProductName());
        resp.setCreatedAt(saved.getCreatedAt());
        resp.setRating(saved.getRating());
        resp.setImageUrl(saved.getReviewPhotoUrl());
        resp.setText(saved.getReviewField());
        resp.setProductLike(saved.getProduct().getProductLike());
        resp.setLiked(false);
        return resp;
    }

    @Override
    @Transactional
    public void delete(Long userId, Long reviewId) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("리뷰를 찾을 수 없습니다."));
        if (!review.getUser().getId().equals(userId)) {
            throw new IllegalStateException("본인이 작성한 리뷰만 삭제할 수 있습니다.");
        }
        reviewRepository.delete(review);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ReviewResponse> getShopReviews(Long shopId, Long viewerUserId, Pageable pageable) {
        if (viewerUserId == null) {
            // 비로그인: isLiked=false
            return reviewRepository.findShopReviews(shopId, pageable);
        }
        // 로그인 사용자: isLiked 계산
        return reviewRepository.findShopReviewsWithLike(shopId, viewerUserId, pageable);
    }
}
