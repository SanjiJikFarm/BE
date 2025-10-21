// 파일: src/main/java/com/example/SanjiBE/service/ProductServiceImpl.java
package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.ProductResponse;
import com.example.SanjiBE.entity.Product;
import com.example.SanjiBE.entity.Review;
import com.example.SanjiBE.repository.ProductLikeRepository;
import com.example.SanjiBE.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductLikeRepository productLikeRepository;

    @Override
    public List<ProductResponse> getProductsByShop(Long shopId, Long userId) {
        List<Product> products = productRepository.findByShop_Id(shopId);

        return products.stream().map(product -> {
            long reviewCount = product.getReviews().size();

            double averageRating = product.getReviews().isEmpty() ? 0.0 :
                    product.getReviews().stream()
                            .mapToInt(Review::getRating)
                            .average()
                            .orElse(0.0);

            boolean isLiked = (userId != null) &&
                    productLikeRepository.existsByUser_IdAndProduct_Id(userId, product.getId());

            return new ProductResponse(
                    product.getId(),
                    product.getProductName(),
                    product.getProductImage(),
                    product.getProductPrice(),
                    reviewCount,
                    averageRating,
                    product.getProductLike(),
                    isLiked
            );
        }).collect(Collectors.toList());
    }

    @Override
    public ProductResponse getProductInShop(Long shopId, Long productId, Long userId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."));

        // 매장 소속 검증
        if (product.getShop() == null || product.getShop().getId() == null || !product.getShop().getId().equals(shopId)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "해당 매장에서 상품을 찾을 수 없습니다.");
        }

        long reviewCount = product.getReviews().size();

        double averageRating = product.getReviews().isEmpty() ? 0.0 :
                product.getReviews().stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0.0);

        boolean isLiked = (userId != null) &&
                productLikeRepository.existsByUser_IdAndProduct_Id(userId, product.getId());

        return new ProductResponse(
                product.getId(),
                product.getProductName(),
                product.getProductImage(),
                product.getProductPrice(),
                reviewCount,
                averageRating,
                product.getProductLike(),
                isLiked
        );
    }
}
