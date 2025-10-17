package com.example.SanjiBE.controller;

import com.example.SanjiBE.dto.ProductResponse;
import com.example.SanjiBE.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/shops/{shopId}/productList") // 기존 경로 유지
@Tag(name = "Product", description = "상품 관련 API")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @Operation(summary = "상품 전체 조회")
    @GetMapping
    public List<ProductResponse> getProductsByShop(
            @PathVariable Long shopId,
            @AuthenticationPrincipal(expression = "id") Long authUserId
    ) {
        return productService.getProductsByShop(shopId, authUserId);
    }

    @Operation(summary = "상품 개별 조회")
    @GetMapping("/{productId}")
    public ProductResponse getProductDetailInShop(
            @PathVariable Long shopId,
            @PathVariable Long productId,
            @AuthenticationPrincipal(expression = "id") Long authUserId
    ) {
        return productService.getProductInShop(shopId, productId, authUserId);
    }
}
