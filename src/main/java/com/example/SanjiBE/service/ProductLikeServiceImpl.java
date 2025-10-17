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

// 추가 import
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

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
        // 클라이언트에서 보낸 sort 키를 안전한 엔티티 경로로 매핑.
        Pageable safe = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                remapSort(pageable.getSort())
        );
        return productLikeRepository.findLikedProductsAsDto(userId, safe);
    }

    private Sort remapSort(Sort sort) {
        if (sort == null || sort.isUnsorted()) {
            return Sort.by(Sort.Direction.DESC, "product.id"); // 기본 정렬
        }
        return Sort.by(
                sort.stream().map(order -> {
                    String prop = order.getProperty();
                    if ("productId".equals(prop) || "id".equals(prop)) prop = "product.id";
                    else if ("createdAt".equals(prop)) prop = "createdAt";
                    else prop = "product.id";
                    return new Sort.Order(order.getDirection(), prop);
                }).toList()
        );
    }
}
