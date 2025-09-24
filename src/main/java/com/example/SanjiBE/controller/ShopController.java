package com.example.SanjiBE.controller;

import com.example.SanjiBE.dto.ShopResponse;
import com.example.SanjiBE.service.ShopService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops")
@RequiredArgsConstructor
public class ShopController {

    private final ShopService shopService;

    // 특정 매장 조회
    @GetMapping("/{shopId}")
    public ShopResponse getShopById(@PathVariable Long shopId) {
        return shopService.getShopById(shopId);
    }

    // 전체 매장 조회
    @GetMapping
    public List<ShopResponse> getAllShops() {
        return shopService.getAllShops();
    }

    // 검색
    @GetMapping(params = "keyword")
    public List<ShopResponse> searchShops(@RequestParam String keyword) {
        return shopService.searchShops(keyword);
    }
}