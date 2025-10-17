package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.ProductResponse;

import java.util.List;

public interface ProductService {
    List<ProductResponse> getProductsByShop(Long shopId, Long userId);
    ProductResponse getProductInShop(Long shopId, Long productId, Long userId);
}
