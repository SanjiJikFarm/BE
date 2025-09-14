package com.example.SanjiBE.controller;

import com.example.SanjiBE.dto.AuthResponse;
import com.example.SanjiBE.dto.LoginRequest;
import com.example.SanjiBE.dto.RegisterRequest;
import com.example.SanjiBE.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*") // CORS 설정 (개발용)
public class AuthController {
    
    @Autowired
    private UserService userService;
    
    /**
     * 회원가입 API
     * @param request 회원가입 요청 정보
     * @return ResponseEntity<AuthResponse> 회원가입 결과
     */
    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        AuthResponse response = userService.register(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * 회원가입 API (GET 방식 - 브라우저 테스트용)
     * @param username 사용자명
     * @param email 이메일
     * @param password 비밀번호
     * @return ResponseEntity<AuthResponse> 회원가입 결과
     */
    @GetMapping("/register")
    public ResponseEntity<AuthResponse> registerGet(
            @RequestParam String username,
            @RequestParam String email,
            @RequestParam String password) {
        
        RegisterRequest request = new RegisterRequest(username, email, password);
        AuthResponse response = userService.register(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * 로그인 API
     * @param request 로그인 요청 정보
     * @return ResponseEntity<AuthResponse> 로그인 결과
     */
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = userService.login(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
    /**
     * 로그인 API (GET 방식 - 브라우저 테스트용)
     * @param username 사용자명
     * @param password 비밀번호
     * @return ResponseEntity<AuthResponse> 로그인 결과
     */
    @GetMapping("/login")
    public ResponseEntity<AuthResponse> loginGet(
            @RequestParam String username,
            @RequestParam String password) {
        
        LoginRequest request = new LoginRequest(username, password);
        AuthResponse response = userService.login(request);
        
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }
    
}



