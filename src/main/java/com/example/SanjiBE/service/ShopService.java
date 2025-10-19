package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.ShopMapResponse;
import com.example.SanjiBE.dto.ShopResponse;

import java.util.List;

public interface ShopService {

    ShopResponse getShopById(Long shopId);

    List<ShopResponse> getAllShops();

    List<ShopResponse> searchShops(String keyword);

    // 기존 시그니처(초기 지도용). 내부적으로 rating 정렬로 위임.
    List<ShopMapResponse> getAllShopsForMap();

    // 기존 시그니처(근처 매장). 내부적으로 distance 정렬로 위임.
    List<ShopMapResponse> getNearbyShops(double lat, double lng, Double radiusKm, String keyword);

    // 신규: 초기 지도용 목록 정렬 지원. sort=distance면 lat/lng 필요.
    List<ShopMapResponse> getAllShopsForMap(String sort, Double lat, Double lng);

    // 신규: 근처 매장 정렬 지원. sort=distance|rating
    List<ShopMapResponse> getNearbyShops(double lat, double lng, Double radiusKm, String keyword, String sort);
}
