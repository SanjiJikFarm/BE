package com.example.SanjiBE.security;

import com.example.SanjiBE.entity.User;
import com.example.SanjiBE.repository.UserRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * UserDetailsService 구현체
 * - username 기준으로 사용자 조회
 * - 기본 권한 ROLE_USER 부여
 * - SecurityContext의 principal을 CustomUserDetails로 반환
 */
@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * username(또는 필요시 이메일)로 사용자 조회 후 CustomUserDetails 반환
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByUsername(username)
                // 이메일 로그인도 허용하려면 레포지토리에 findByEmail 추가 후 아래 주석 해제
                // .or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        // 핵심: 스프링 기본 User가 아니라 CustomUserDetails를 반환
        return new CustomUserDetails(u, authorities);
    }
}
