package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.AuthResponse;
import com.example.SanjiBE.dto.LoginRequest;
import com.example.SanjiBE.dto.RegisterRequest;
import com.example.SanjiBE.entity.User;
import com.example.SanjiBE.repository.UserRepository;
import com.example.SanjiBE.security.JwtTokenProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @Value("${jwt.refreshExpiration}")
    private long refreshExpirationMs;

    @Autowired
    private RefreshTokenService refreshTokenService;


    public AuthResponse register(RegisterRequest request) {
        try {
            if (userRepository.existsByUsername(request.getUsername())) {
                return new AuthResponse(false, "이미 사용 중인 사용자명입니다.");
            }

            String encodedPassword = passwordEncoder.encode(request.getPassword());
            User user = new User(request.getUsername(), encodedPassword);
            User saved = userRepository.save(user);

            // 회원가입 응답(토큰은 발급하지 않음)
            return new AuthResponse(true, "회원가입이 성공적으로 완료되었습니다.",
                    saved.getUsername(), null);
        } catch (Exception e) {
            return new AuthResponse(false, "회원가입 중 오류가 발생했습니다: " + e.getMessage());
        }
    }

    public User findByUsername(String username) { return userRepository.findByUsername(username).orElse(null); }

    public AuthResponse login(LoginRequest request) {
        try {
            User user = userRepository.findByUsername(request.getUsername()).orElse(null);
            if (user == null) {
                return new AuthResponse(false, "사용자명 또는 비밀번호가 올바르지 않습니다.");
            }
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return new AuthResponse(false, "사용자명 또는 비밀번호가 올바르지 않습니다.");
            }

            // 액세스 토큰
            String accessToken = jwtTokenProvider.generateToken(user.getUsername());

// 리프레시 토큰
            String refreshToken = jwtTokenProvider.generateRefreshToken(
                    user.getUsername(),
                    refreshExpirationMs   // 아래 주입 필요
            );

// DB 저장
            refreshTokenService.issue(user.getUsername(), refreshToken, refreshExpirationMs);

// 응답
            AuthResponse res = new AuthResponse(
                    true,
                    "로그인이 성공적으로 완료되었습니다.",
                    user.getUsername(),
                    accessToken
            );
            res.setRefreshToken(refreshToken);
            return res;

        } catch (Exception e) {
            return new AuthResponse(false, "로그인 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
}
