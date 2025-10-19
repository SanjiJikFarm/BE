package com.example.SanjiBE.controller;

import com.example.SanjiBE.dto.ShopMapResponse;
import com.example.SanjiBE.dto.ShopResponse;
import com.example.SanjiBE.service.ShopService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
@Tag(name = "Shops", description = "매장 관련 API")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    // 특정 매장 조회
    @Operation(summary = "특정 매장 조회")
    @GetMapping("/{shopId}")
    public ShopResponse getShopById(@PathVariable Long shopId) {
        return shopService.getShopById(shopId);
    }

    // 전체 매장 조회
    @Operation(summary = "전체 매장 조회")
    @GetMapping
    public List<ShopResponse> getAllShops() {
        return shopService.getAllShops();
    }

    // 검색
    @Operation(summary = "매장 검색")
    @GetMapping(params = "keyword")
    public List<ShopResponse> searchShops(@RequestParam String keyword) {
        return shopService.searchShops(keyword);
    }

    // 좌표 보유 매장 전체
    @Operation(summary = "지도용 전체 매장", description = "sort=distance | rating. rating 기본.")
    @GetMapping("/map")
    public List<ShopMapResponse> getAllShopsForMap(
            @RequestParam(required = false, defaultValue = "rating") String sort,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng
    ) {
        // sort=distance일 때만 lat/lng 요구
        return shopService.getAllShopsForMap(sort, lat, lng);
    }

    // 거리순/평점순 지원
    @Operation(summary = "거리 기준 매장 조회", description = "sort=distance|rating. distance 기본. radiusKm/keyword 옵션")
    @GetMapping("/map/nearby")
    public List<ShopMapResponse> getNearbyShops(
            @RequestParam double lat,
            @RequestParam double lng,
            @RequestParam(required = false) Double radiusKm,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false, defaultValue = "distance") String sort
    ) {
        return shopService.getNearbyShops(lat, lng, radiusKm, keyword, sort);
    }
}