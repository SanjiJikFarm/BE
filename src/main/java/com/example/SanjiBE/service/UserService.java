package com.example.SanjiBE.service;

import com.example.SanjiBE.dto.AuthResponse;
import com.example.SanjiBE.dto.LoginRequest;
import com.example.SanjiBE.dto.RegisterRequest;
import com.example.SanjiBE.entity.User;
import com.example.SanjiBE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    /**
     * 사용자 회원가입
     * @param request 회원가입 요청 정보
     * @return AuthResponse 회원가입 결과
     */
    public AuthResponse register(RegisterRequest request) {
        try {
            // 1. 사용자명 중복 확인
            if (userRepository.existsByUsername(request.getUsername())) {
                return new AuthResponse(false, "이미 사용 중인 사용자명입니다.");
            }
            
            // 2. 이메일 중복 확인
            if (userRepository.existsByEmail(request.getEmail())) {
                return new AuthResponse(false, "이미 사용 중인 이메일입니다.");
            }
            
            // 3. 비밀번호 암호화
            String encodedPassword = passwordEncoder.encode(request.getPassword());
            
            // 4. 사용자 엔티티 생성
            User user = new User(
                request.getUsername(),
                request.getEmail(),
                encodedPassword
            );
            
            // 5. 데이터베이스에 저장
            User savedUser = userRepository.save(user);
            
            // 6. 성공 응답 반환
            return new AuthResponse(
                true,
                "회원가입이 성공적으로 완료되었습니다.",
                savedUser.getUsername(),
                savedUser.getEmail()
            );
            
        } catch (Exception e) {
            return new AuthResponse(false, "회원가입 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
    /**
     * 사용자명으로 사용자 찾기
     * @param username 사용자명
     * @return User 사용자 엔티티
     */
    public User findByUsername(String username) {
        return userRepository.findByUsername(username).orElse(null);
    }
    
    /**
     * 이메일로 사용자 찾기
     * @param email 이메일
     * @return User 사용자 엔티티
     */
    public User findByEmail(String email) {
        return userRepository.findByEmail(email).orElse(null);
    }
    
    /**
     * 사용자 로그인
     * @param request 로그인 요청 정보
     * @return AuthResponse 로그인 결과
     */
    public AuthResponse login(LoginRequest request) {
        try {
            // 1. 사용자명으로 사용자 찾기
            User user = userRepository.findByUsername(request.getUsername()).orElse(null);
            
            if (user == null) {
                return new AuthResponse(false, "사용자명 또는 비밀번호가 올바르지 않습니다.");
            }
            
            // 2. 비밀번호 확인
            if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
                return new AuthResponse(false, "사용자명 또는 비밀번호가 올바르지 않습니다.");
            }
            
            // 3. 성공 응답 반환
            return new AuthResponse(
                true,
                "로그인이 성공적으로 완료되었습니다.",
                user.getUsername(),
                user.getEmail()
            );
            
        } catch (Exception e) {
            return new AuthResponse(false, "로그인 중 오류가 발생했습니다: " + e.getMessage());
        }
    }
    
}



