package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.ShopMapResponse;
import com.example.SanjiBE.dto.ShopResponse;
import com.example.SanjiBE.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

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

    // 초기 지도용: 기본 rating
    @Override
    public List<ShopMapResponse> getAllShopsForMap() {
        return getAllShopsForMap("rating", null, null);
    }

    // 근처: 기본 distance
    @Override
    public List<ShopMapResponse> getNearbyShops(double lat, double lng, Double radiusKm, String keyword) {
        return getNearbyShops(lat, lng, radiusKm, keyword, "distance");
    }

    // /map 정렬 지원
    @Override
    public List<ShopMapResponse> getAllShopsForMap(String sort, Double lat, Double lng) {
        String s = normalizeSort(sort, "rating");
        if ("distance".equals(s)) {
            if (lat == null || lng == null) {
                throw new IllegalArgumentException("sort=distance 는 lat,lng가 필요합니다");
            }
            return shopRepository.findNearbyForMapDistance(lat, lng, null, null);
        }
        return shopRepository.findAllForMapOrderByRating();
    }

    // /map/nearby 정렬 지원
    @Override
    public List<ShopMapResponse> getNearbyShops(double lat, double lng, Double radiusKm, String keyword, String sort) {
        String s = normalizeSort(sort, "distance");
        if ("rating".equals(s)) {
            return shopRepository.findNearbyForMapOrderByRating(lat, lng, radiusKm, keyword);
        }
        return shopRepository.findNearbyForMapDistance(lat, lng, radiusKm, keyword);
    }

    // 정렬 파라미터 표준화
    private String normalizeSort(String sort, String defaultSort) {
        if (sort == null) return defaultSort;
        String s = sort.trim().toLowerCase();
        if ("distance".equals(s) || "rating".equals(s)) return s;
        return defaultSort;
    }

    @Override
    public List<ShopMapResponse> searchShops(String keyword, String sort, Double lat, Double lng, Double radiusKm) {
        String key = (keyword == null) ? "" : keyword.trim();
        String s = normalizeSort(sort, "rating");

        if ("distance".equals(s)) {
            if (lat == null || lng == null) {
                throw new IllegalArgumentException("sort=distance 는 lat,lng가 필요합니다");
            }
            return shopRepository.searchForMapOrderByDistance(key, lat, lng, radiusKm);
        }
        return shopRepository.searchForMapOrderByRating(key);
    }
}
