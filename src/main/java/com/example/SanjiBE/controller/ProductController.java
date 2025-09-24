package com.example.SanjiBE.controller;

import com.example.SanjiBE.dto.ProductResponse;
import com.example.SanjiBE.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops/{shopId}/product")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping
    public List<ProductResponse> getProductsByShop(
            @PathVariable Long shopId,
            @RequestParam(required = false) Long userId // 즐겨찾기 여부 확인용
    ) {
        return productService.getProductsByShop(shopId, userId);
    }
}
