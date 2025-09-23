package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.ProductResponse;
import com.example.SanjiBE.entity.Product;
import com.example.SanjiBE.entity.Review;
import com.example.SanjiBE.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

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

            // TODO: userId 기준으로 즐겨찾기 여부 판단 (지금은 임시 false)
            boolean isLiked = false;

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
}
