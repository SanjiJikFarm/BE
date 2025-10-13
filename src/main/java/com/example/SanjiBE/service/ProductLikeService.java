package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.ProductResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductLikeService {
    boolean like(Long productId, Long userId);
    boolean unlike(Long productId, Long userId);

    Page<ProductResponse> getMyLikes(Long userId, Pageable pageable);
}
