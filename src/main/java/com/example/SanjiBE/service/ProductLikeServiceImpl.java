package com.example.SanjiBE.service;

import com.example.SanjiBE.entity.Product;
import com.example.SanjiBE.entity.ProductLike;
import com.example.SanjiBE.entity.User;
import com.example.SanjiBE.repository.ProductLikeRepository;
import com.example.SanjiBE.repository.ProductRepository;
import com.example.SanjiBE.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
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
        // 엔티티 확인
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found: id=" + productId));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found: id=" + userId));

        // 이미 찜했는지 확인
        boolean exists = productLikeRepository.existsByUser_IdAndProduct_Id(userId, productId);
        if (exists) {
            return true; // 이미 찜 상태 → 아무 작업 없음
        }

        // 레코드 생성
        ProductLike like = new ProductLike();
        like.setProduct(product);
        like.setUser(user);
        productLikeRepository.save(like);

        // 카운트 증가
        product.setProductLike(product.getProductLike() + 1);
        // 더티체킹으로 flush

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
        if (!exists) {
            return false;
        }

        // 삭제
        productLikeRepository.deleteByUser_IdAndProduct_Id(userId, productId);

        // 카운터 감소(0 미만 방지)
        int current = product.getProductLike();
        product.setProductLike(Math.max(0, current - 1));

        return false;
    }
}
