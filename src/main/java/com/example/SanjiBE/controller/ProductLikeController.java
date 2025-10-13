package com.example.SanjiBE.controller;

import com.example.SanjiBE.service.ProductLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product")
@Tag(name = "Product Like", description = "상품 찜 관련 API")
public class ProductLikeController {

    private final ProductLikeService productLikeService;

    @Operation(summary = "상품 찜하기", description = "이미 찜 상태면 그대로 유지")
    @GetMapping("/{productId}/like")
    public ResponseEntity<LikeResponse> like(
            @PathVariable Long productId,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        boolean liked = productLikeService.like(productId, userId);
        return ResponseEntity.ok(new LikeResponse(liked));
    }

    @Operation(summary = "상품 찜 해제", description = "이미 not 찜 상태면 그대로 유지")
    @DeleteMapping("/{productId}/like")
    public ResponseEntity<LikeResponse> unlike(
            @PathVariable Long productId,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        boolean liked = productLikeService.unlike(productId, userId);
        return ResponseEntity.ok(new LikeResponse(liked));
    }

    // 최소 응답 DTO
    private record LikeResponse(boolean liked) {}
}
