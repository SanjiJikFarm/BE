package com.example.SanjiBE.service;

public interface ProductLikeService {
    boolean like(Long productId, Long userId);
    boolean unlike(Long productId, Long userId);
}
