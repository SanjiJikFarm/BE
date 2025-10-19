package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.ShopMapResponse;
import com.example.SanjiBE.dto.ShopResponse;

import java.util.List;

public interface ShopService {

    ShopResponse getShopById(Long shopId);

    List<ShopResponse> getAllShops();

    List<ShopResponse> searchShops(String keyword);

    // 기존 시그니처: 초기 지도용(기본 rating)
    List<ShopMapResponse> getAllShopsForMap();

    // 기존 시그니처: 근처(기본 distance)
    List<ShopMapResponse> getNearbyShops(double lat, double lng, Double radiusKm, String keyword);

    // 정렬 지원: /map
    List<ShopMapResponse> getAllShopsForMap(String sort, Double lat, Double lng);

    // 정렬 지원: /map/nearby
    List<ShopMapResponse> getNearbyShops(double lat, double lng, Double radiusKm, String keyword, String sort);
}
