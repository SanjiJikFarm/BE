package com.example.SanjiBE.dto;

public class AuthResponse {
    
    private boolean success;
    private String message;
    private String username;
    private String email;
    
    // 기본 생성자
    public AuthResponse() {}
    
    // 성공 응답 생성자
    public AuthResponse(boolean success, String message, String username, String email) {
        this.success = success;
        this.message = message;
        this.username = username;
        this.email = email;
    }
    
    // 실패 응답 생성자
    public AuthResponse(boolean success, String message) {
        this.success = success;
        this.message = message;
    }
    
    // Getter와 Setter
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getMessage() {
        return message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
    @Override
    public String toString() {
        return "AuthResponse{" +
                "success=" + success +
                ", message='" + message + '\'' +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}



