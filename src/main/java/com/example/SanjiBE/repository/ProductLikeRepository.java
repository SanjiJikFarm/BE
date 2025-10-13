package com.example.SanjiBE.repository;

import com.example.SanjiBE.dto.ProductResponse;
import com.example.SanjiBE.entity.ProductLike;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    boolean existsByUser_IdAndProduct_Id(Long userId, Long productId);
    int countByProduct_Id(Long productId);
    void deleteByUser_IdAndProduct_Id(Long userId, Long productId);

    // 내가 찜한 상품 목록을 ProductResponse DTO로 바로 조회(집계 포함)
    @Query(
            value = """
        select new com.example.SanjiBE.dto.ProductResponse(
          p.id, p.productName, p.productImage, p.productPrice,
          count(r.id), coalesce(avg(r.rating), 0.0),
          p.productLike, true
        )
        from ProductLike pl
          join pl.product p
          left join p.reviews r
        where pl.user.id = :userId
        group by p.id, p.productName, p.productImage, p.productPrice, p.productLike
        """,
            countQuery = """
        select count(p.id)
        from ProductLike pl
          join pl.product p
        where pl.user.id = :userId
        """
    )
    Page<ProductResponse> findLikedProductsAsDto(@Param("userId") Long userId, Pageable pageable);
}
