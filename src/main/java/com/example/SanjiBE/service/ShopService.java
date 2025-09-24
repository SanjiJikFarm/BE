package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.ShopResponse;

import java.util.List;

public interface ShopService {
    ShopResponse getShopById(Long shopId);
    List<ShopResponse> getAllShops();

    List<ShopResponse> searchShops(String keyword);
}