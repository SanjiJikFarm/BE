package com.example.SanjiBE.repository;

import com.example.SanjiBE.dto.ReviewResponse;
import com.example.SanjiBE.entity.Review;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    /**
     * 내가 구매한 상품에 대해 내가 작성한 리뷰 목록
     * - r.user.id = :userId (내가 작성)
     * - EXISTS(구매이력) 조건으로 "구매한 상품" 보장
     */
    @Query("""
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
        """)
    List<ReviewResponse> findMyReviews(@Param("userId") Long userId);
    boolean existsByUser_IdAndProduct_Id(Long userId, Long productId);
}