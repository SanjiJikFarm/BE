package com.example.SanjiBE.repository;

import com.example.SanjiBE.entity.ProductLike;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLikeRepository extends JpaRepository<ProductLike, Long> {
    boolean existsByUser_IdAndProduct_Id(Long userId, Long productId);
    int countByProduct_Id(Long productId);
    void deleteByUser_IdAndProduct_Id(Long userId, Long productId);
}