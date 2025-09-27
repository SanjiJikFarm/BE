package com.example.SanjiBE.dto;

public class AuthResponse {

    private boolean success;
    private String message;
    private String username;
    private String email;
    private String token; // JWT 액세스 토큰
    private String refreshToken;

    public AuthResponse() {}

    public AuthResponse(boolean success, String message, String username, String email, String token) {
        this.success = success;
        this.message = message;
        this.username = username;
        this.email = email;
        this.token = token;
    }

    public AuthResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    // getters / setters
    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public String getRefreshToken() { return refreshToken; }     // ← 추가
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; } // ← 추가


    @Override
    public String toString() {
        return "AuthResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", token='" + token + '\'' +
                ", refreshToken='" + refreshToken + '\'' +
                '}';
    }
}
