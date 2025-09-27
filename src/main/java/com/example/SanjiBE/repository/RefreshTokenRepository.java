package com.example.SanjiBE.repository;

import com.example.SanjiBE.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);
    void deleteByToken(String token);                 // 단일 토큰 로그아웃
    void deleteAllByUsername(String username);        // 계정 전체 로그아웃
}
