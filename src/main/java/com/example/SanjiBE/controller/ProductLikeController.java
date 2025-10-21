package com.example.SanjiBE.controller;

import com.example.SanjiBE.dto.ProductResponse;
import org.springframework.security.access.AccessDeniedException;
import com.example.SanjiBE.service.ProductLikeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "Product Like", description = "상품 찜 관련 API")
public class ProductLikeController {

    private final ProductLikeService productLikeService;

    @Operation(summary = "상품 찜하기")
    @GetMapping("/products/{productId}/like")
    public ResponseEntity<LikeResponse> like(
            @PathVariable Long productId,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        boolean liked = productLikeService.like(productId, userId);
        return ResponseEntity.ok(new LikeResponse(liked));
    }

    @Operation(summary = "상품 찜 해제")
    @DeleteMapping("/products/{productId}/like")
    public ResponseEntity<LikeResponse> unlike(
            @PathVariable Long productId,
            @AuthenticationPrincipal(expression = "id") Long userId
    ) {
        boolean liked = productLikeService.unlike(productId, userId);
        return ResponseEntity.ok(new LikeResponse(liked));
    }

    @Operation(summary = "내 찜 목록", description = "인증 사용자 본인의 찜 목록을 조회")
    @GetMapping("/users/likes")
    public Page<ProductResponse> getMyLikes(
            @AuthenticationPrincipal(expression = "id") Long authUserId,
            @ParameterObject Pageable pageable
    ) {
        if (authUserId == null) {
            throw new AccessDeniedException("unauthorized");
        }
        return productLikeService.getMyLikes(authUserId, pageable);
    }

    public static record LikeResponse(boolean liked) {}
}
