package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.ShopMapResponse;
import com.example.SanjiBE.dto.ShopResponse;
import com.example.SanjiBE.entity.Review;
import com.example.SanjiBE.entity.Shop;
import com.example.SanjiBE.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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

    // 초기 로딩
    @Override
    public List<ShopMapResponse> getAllShopsForMap() {
        return shopRepository.findAllForMapWithStats();
    }

    // 거리순 조회
    @Override
    public List<ShopMapResponse> getNearbyShops(double lat, double lng, Double radiusKm, String keyword) {
        List<Object[]> rows = shopRepository.findNearbyForMapNative(lat, lng, radiusKm, keyword);
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
}