// 파일 경로: src/main/java/com/example/SanjiBE/security/CustomUserDetailsService.java
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
 * - 권한: 기본 ROLE_USER 부여 (엔티티에 역할 필드 없음)
 * - 생성자 주입 사용
 * - readOnly 트랜잭션 적용
 */
@Service
@Transactional(readOnly = true)
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    // 생성자 주입
    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * username으로 사용자 조회 후 UserDetails 반환
     * 필요하면 이메일 로그인도 허용하도록 findByEmail 추가 가능
     */
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User u = userRepository.findByUsername(username)
                // 이메일로도 허용하려면 아래 주석 해제 + 레포지토리에 findByEmail 존재해야 함
                //.or(() -> userRepository.findByEmail(username))
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        // 기본 권한만 부여
        List<SimpleGrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_USER"));

        // 계정 상태 플래그
        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        return new org.springframework.security.core.userdetails.User(
                u.getUsername(),
                u.getPassword(),
                enabled,
                accountNonExpired,
                credentialsNonExpired,
                accountNonLocked,
                authorities
        );
    }
}
