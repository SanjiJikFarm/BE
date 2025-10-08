package com.example.SanjiBE.repository;

import com.example.SanjiBE.dto.ReviewResponse;
import com.example.SanjiBE.entity.Review;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 내가 구매한 상품에 대해 내가 작성한 리뷰 목록 (Page)
     * - r.user.id = :userId
     * - EXISTS(구매이력)으로 "구매자" 보장
     * - isLiked 계산: 로그인 사용자 기준
     */
    @Query(value = """
        SELECT new com.example.SanjiBE.dto.ReviewResponse(
            r.id,
            p.id,
            p.productName,
            r.createdAt,
            r.rating,
            r.reviewPhotoUrl,
            r.reviewField,
            p.productLike,
            CASE WHEN
                 (SELECT COUNT(pl) FROM ProductLike pl
                  WHERE pl.product = p AND pl.user.id = :userId) > 0
            THEN true ELSE false END
        )
        FROM Review r
        JOIN r.product p
        WHERE r.user.id = :userId
          AND EXISTS (
              SELECT 1 FROM Purchase pu
              JOIN pu.receipt rc
              WHERE pu.product = p
                AND rc.user.id = :userId
          )
        ORDER BY r.createdAt DESC
        """,
            countQuery = """
        SELECT COUNT(r)
        FROM Review r
        JOIN r.product p
        WHERE r.user.id = :userId
          AND EXISTS (
              SELECT 1 FROM Purchase pu
              JOIN pu.receipt rc
              WHERE pu.product = p
                AND rc.user.id = :userId
          )
        """)
    Page<ReviewResponse> findMyReviews(@Param("userId") Long userId, Pageable pageable);

    /**
     * 매장 리뷰 목록 (Page) - 익명/비로그인용: isLiked=false 고정
     */
    @Query(value = """
        SELECT new com.example.SanjiBE.dto.ReviewResponse(
            r.id,
            p.id,
            p.productName,
            r.createdAt,
            r.rating,
            r.reviewPhotoUrl,
            r.reviewField,
            p.productLike,
            false
        )
        FROM Review r
        JOIN r.product p
        JOIN p.shop s
        WHERE s.id = :shopId
        ORDER BY r.createdAt DESC
        """,
            countQuery = """
        SELECT COUNT(r)
        FROM Review r
        JOIN r.product p
        JOIN p.shop s
        WHERE s.id = :shopId
        """)
    Page<ReviewResponse> findShopReviews(@Param("shopId") Long shopId, Pageable pageable);

    /**
     * 매장 리뷰 목록 (Page) - 로그인 사용자용: isLiked 계산
     */
    @Query(value = """
        SELECT new com.example.SanjiBE.dto.ReviewResponse(
            r.id,
            p.id,
            p.productName,
            r.createdAt,
            r.rating,
            r.reviewPhotoUrl,
            r.reviewField,
            p.productLike,
            CASE WHEN
                 (SELECT COUNT(pl) FROM ProductLike pl
                  WHERE pl.product = p AND pl.user.id = :userId) > 0
            THEN true ELSE false END
        )
        FROM Review r
        JOIN r.product p
        JOIN p.shop s
        WHERE s.id = :shopId
        ORDER BY r.createdAt DESC
        """,
            countQuery = """
        SELECT COUNT(r)
        FROM Review r
        JOIN r.product p
        JOIN p.shop s
        WHERE s.id = :shopId
        """)
    Page<ReviewResponse> findShopReviewsWithLike(@Param("shopId") Long shopId,
                                                 @Param("userId") Long userId,
                                                 Pageable pageable);

    boolean existsByUser_IdAndProduct_Id(Long userId, Long productId);

    boolean existsByIdAndUser_Id(Long reviewId, Long userId);
}
