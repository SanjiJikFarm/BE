package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.ProductResponse;
import com.example.SanjiBE.entity.Product;
import com.example.SanjiBE.entity.ProductLike;
import com.example.SanjiBE.entity.User;
import com.example.SanjiBE.repository.ProductLikeRepository;
import com.example.SanjiBE.repository.ProductRepository;
import com.example.SanjiBE.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductLikeServiceImpl implements ProductLikeService {

    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ProductLikeRepository productLikeRepository;

    @Transactional
    @Override
    public boolean like(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: id=" + productId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: id=" + userId));

        boolean exists = productLikeRepository.existsByUser_IdAndProduct_Id(userId, productId);
        if (exists) return true;

        ProductLike like = new ProductLike();
        like.setProduct(product);
        like.setUser(user);
        productLikeRepository.save(like);

        product.setProductLike(product.getProductLike() + 1);
        return true;
    }

    @Transactional
    @Override
    public boolean unlike(Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: id=" + productId));
        userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: id=" + userId));

        boolean exists = productLikeRepository.existsByUser_IdAndProduct_Id(userId, productId);
        if (!exists) return false;

        productLikeRepository.deleteByUser_IdAndProduct_Id(userId, productId);
        product.setProductLike(Math.max(0, product.getProductLike() - 1));
        return false;
    }

    @Transactional(readOnly = true)
    @Override
    public Page<ProductResponse> getMyLikes(Long userId, Pageable pageable) {
        return productLikeRepository.findLikedProductsAsDto(userId, pageable);
    }
}
