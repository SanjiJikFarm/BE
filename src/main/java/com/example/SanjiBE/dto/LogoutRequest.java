package com.example.SanjiBE.dto;

import jakarta.validation.constraints.NotBlank;

public class LogoutRequest {
    @NotBlank(message = "refreshToken은 필수입니다.")
    private String refreshToken;

    public LogoutRequest() {}
    public LogoutRequest(String refreshToken) { this.refreshToken = refreshToken; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}