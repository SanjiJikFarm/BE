package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.ShopMapResponse;
import com.example.SanjiBE.dto.ShopResponse;
import com.example.SanjiBE.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    // 기존 시그니처: 기본 rating 정렬로 위임
    @Override
    public List<ShopMapResponse> getAllShopsForMap() {
        return getAllShopsForMap("rating", null, null);
    }

    // 기존 시그니처: 기본 distance 정렬로 위임
    @Override
    public List<ShopMapResponse> getNearbyShops(double lat, double lng, Double radiusKm, String keyword) {
        return getNearbyShops(lat, lng, radiusKm, keyword, "distance");
    }

    // 신규: 초기 목록 정렬 지원
    @Override
    public List<ShopMapResponse> getAllShopsForMap(String sort, Double lat, Double lng) {
        String s = normalizeSort(sort, "rating");
        if ("distance".equals(s)) {
            if (lat == null || lng == null) {
                throw new IllegalArgumentException("sort=distance 는 lat,lng가 필요합니다");
            }
            List<Object[]> rows = shopRepository.findNearbyForMapNative(lat, lng, null, null);
            return mapNearbyRows(rows);
        }
        return shopRepository.findAllForMapOrderByRating();
    }

    // 신규: 근처 정렬 지원
    @Override
    public List<ShopMapResponse> getNearbyShops(double lat, double lng, Double radiusKm, String keyword, String sort) {
        String s = normalizeSort(sort, "distance");
        if ("rating".equals(s)) {
            List<Object[]> rows = shopRepository.findNearbyForMapOrderByRating(lat, lng, radiusKm, keyword);
            return mapNearbyRows(rows);
        }
        List<Object[]> rows = shopRepository.findNearbyForMapNative(lat, lng, radiusKm, keyword);
        return mapNearbyRows(rows);
    }

    // 공통 매핑
    private List<ShopMapResponse> mapNearbyRows(List<Object[]> rows) {
        List<ShopMapResponse> result = new ArrayList<>(rows.size());
        for (Object[] o : rows) {
            Long   id          = ((Number) o[0]).longValue();
            String shopName    = (String) o[1];
            String shopImage   = (String) o[2];
            String address     = (String) o[3];
            Double avgRating   = ((Number) o[4]).doubleValue();
            Long   reviewCount = ((Number) o[5]).longValue();
            Double latitude    = o[6] != null ? ((Number) o[6]).doubleValue() : null;
            Double longitude   = o[7] != null ? ((Number) o[7]).doubleValue() : null;
            Double distanceKm  = ((Number) o[8]).doubleValue();

            result.add(new ShopMapResponse(
                    id, shopName, shopImage, address, avgRating, reviewCount,
                    latitude, longitude, distanceKm
            ));
        }
        return result;
    }

    // sort 파라미터 정규화
    private String normalizeSort(String sort, String defaultSort) {
        if (sort == null) return defaultSort;
        String s = sort.trim().toLowerCase();
        if ("distance".equals(s) || "rating".equals(s)) return s;
        return defaultSort;
    }
}
