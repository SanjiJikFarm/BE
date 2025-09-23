package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.ShopResponse;
import com.example.SanjiBE.entity.Review;
import com.example.SanjiBE.entity.Shop;
import com.example.SanjiBE.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShopServiceImpl implements ShopService {

    private final ShopRepository shopRepository;

    @Override
    public ShopResponse getShopById(Long shopId) {
        Shop shop = shopRepository.findById(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));

        double avgRating = shop.getReviews().isEmpty() ?
                0.0 :
                shop.getReviews().stream()
                        .mapToInt(Review::getRating)
                        .average()
                        .orElse(0.0);

        return new ShopResponse(
                shop.getId(),
                shop.getShopName(),
                shop.getShopImage(),
                shop.getAddress(),
                avgRating
        );
    }

    @Override
    public List<ShopResponse> getAllShops() {
        return shopRepository.findAll().stream().map(shop -> {
            double avgRating = shop.getReviews().isEmpty() ?
                    0.0 :
                    shop.getReviews().stream()
                            .mapToInt(Review::getRating)
                            .average()
                            .orElse(0.0);

            return new ShopResponse(
                    shop.getId(),
                    shop.getShopName(),
                    shop.getShopImage(),
                    shop.getAddress(),
                    avgRating
            );
        }).collect(Collectors.toList());
    }
}

