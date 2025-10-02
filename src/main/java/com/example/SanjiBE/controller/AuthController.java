// src/main/java/com/example/SanjiBE/controller/AuthController.java
package com.example.SanjiBE.controller;

import com.example.SanjiBE.dto.AuthResponse;
import com.example.SanjiBE.dto.LoginRequest;
import com.example.SanjiBE.dto.RefreshRequest;
import com.example.SanjiBE.dto.RegisterRequest;
import com.example.SanjiBE.entity.RefreshToken;
import com.example.SanjiBE.security.JwtTokenProvider;
import com.example.SanjiBE.service.RefreshTokenService;
import com.example.SanjiBE.service.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Authorization", description = "로그인/회원가입")
public class AuthController {

    @Autowired
    private UserService userService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private RefreshTokenService refreshTokenService;

    @Value("${jwt.refreshExpiration:1209600000}") // 기본 14일(ms)
    private long refreshExpirationMs;

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    // 액세스 재발급(+회전)
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refresh(@Valid @RequestBody RefreshRequest req) {
        try {
            var stored = refreshTokenService.validateUsable(req.getRefreshToken());
            if (stored == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body(new AuthResponse(false, "유효하지 않은 리프레시 토큰입니다."));
            }

            String username = jwtTokenProvider.getUsername(req.getRefreshToken());
            String newAccess = jwtTokenProvider.generateToken(username);
            String newRefresh = jwtTokenProvider.generateRefreshToken(username, refreshExpirationMs);

            refreshTokenService.rotate(
                    req.getRefreshToken(),
                    new RefreshToken(newRefresh, username,
                            java.time.Instant.now().plusMillis(refreshExpirationMs))
            );

            AuthResponse res = new AuthResponse(true, "토큰 재발급 완료", username, newAccess);
            res.setRefreshToken(newRefresh);
            return ResponseEntity.ok(res);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse(false, "토큰 재발급 실패: " + e.getMessage()));
        }
    }

    // 로그아웃: 전달된 refreshToken 폐기
    @PostMapping("/logout")
    public ResponseEntity<AuthResponse> logout(@Valid @RequestBody com.example.SanjiBE.dto.LogoutRequest req) {
        refreshTokenService.revokeByToken(req.getRefreshToken());
        return ResponseEntity.ok(new AuthResponse(true, "로그아웃 완료"));
    }
}
