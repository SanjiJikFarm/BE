// src/main/java/com/example/SanjiBE/service/RefreshTokenService.java
package com.example.SanjiBE.service;

import com.example.SanjiBE.entity.RefreshToken;
import com.example.SanjiBE.repository.RefreshTokenRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repo;

    public RefreshTokenService(RefreshTokenRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public void issue(String username, String token, long ttlMillis) {
        repo.save(new RefreshToken(token, username, Instant.now().plusMillis(ttlMillis)));
    }

    @Transactional(readOnly = true)
    public RefreshToken validateUsable(String token) {
        return repo.findByToken(token)
                .filter(t -> t.getExpiresAt().isAfter(Instant.now()))
                .orElse(null);
    }

    @Transactional
    public void rotate(String oldToken, RefreshToken newToken) {
        repo.deleteByToken(oldToken);
        repo.save(newToken);
    }

    // 이름을 명확히 변경: 단일 토큰 폐기
    @Transactional
    public void revokeByToken(String token) {
        repo.deleteByToken(token);
    }
}
