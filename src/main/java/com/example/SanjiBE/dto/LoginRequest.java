package com.example.SanjiBE.dto;

import jakarta.validation.constraints.NotBlank;

public class LoginRequest {
    @NotBlank(message = "사용자명 입력하세요.")
    private String username;

    @NotBlank(message = "비밀번호 입력하세요.")
    private String password;

    // 기본 생성자
    public LoginRequest() {}

    // 모든 필드를 받는 생성자
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter 메서드
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    // Setter 메서드
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
