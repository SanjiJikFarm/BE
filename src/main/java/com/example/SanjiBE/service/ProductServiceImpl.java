package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.ProductResponse;
import com.example.SanjiBE.entity.Product;
import com.example.SanjiBE.entity.Review;
import com.example.SanjiBE.repository.ProductLikeRepository;
import com.example.SanjiBE.repository.ProductRepository;
import com.example.SanjiBE.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
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

            // userId가 주어진 경우에만 isLiked 계산
            boolean isLiked = (userId != null) &&
                    productLikeRepository.existsByUser_IdAndProduct_Id(userId, product.getId());

            return new ProductResponse(
                    product.getId(),
                    product.getProductName(),
                    product.getProductImage(),
                    product.getProductPrice(),
                    reviewCount,
                    averageRating,
                    product.getProductLike(), // 정수 카운터 필드 사용
                    isLiked
            );
        }).collect(Collectors.toList());
    }
}
