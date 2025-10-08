package com.example.SanjiBE.security;

import com.example.SanjiBE.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

/**
 * SecurityContext에 저장될 Principal.
 * 컨트롤러에서 @AuthenticationPrincipal(expression = "id") 로 userId를 바로 읽을 수 있도록 getId() 제공.
 */
public class CustomUserDetails implements UserDetails {

    private final User user;
    private final List<SimpleGrantedAuthority> authorities;

    public CustomUserDetails(User user, List<SimpleGrantedAuthority> authorities) {
        this.user = user;
        this.authorities = authorities;
    }

    /** 컨트롤러 SpEL(@AuthenticationPrincipal(expression="id"))에서 사용할 ID */
    public Long getId() {
        return user.getId();
    }

    public User getUser() {
        return user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        // 반드시 해시된 비밀번호가 User에 저장되어 있어야 한다.
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // 현재 인증 식별자를 username으로 사용. 이메일을 쓴다면 user.getEmail()로 교체.
        return user.getUsername();
    }

    @Override public boolean isAccountNonExpired() { return true; }
    @Override public boolean isAccountNonLocked() { return true; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled() { return true; }
}
