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
        return shopRepository.findOneWithStats(shopId)
                .orElseThrow(() -> new RuntimeException("Shop not found"));
    }

    @Override
    public List<ShopResponse> getAllShops() {
        return shopRepository.findAllWithStats();
    }

    @Override
    public List<ShopResponse> searchShops(String keyword) {
        String key = (keyword == null) ? "" : keyword.trim();
        return shopRepository.searchWithStats(key);
    }
}