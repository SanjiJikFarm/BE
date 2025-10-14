package com.example.SanjiBE.controller;

import com.example.SanjiBE.dto.GetS3Url;
import com.example.SanjiBE.entity.User;
import com.example.SanjiBE.repository.UserRepository;
import com.example.SanjiBE.security.CustomUserDetails;
import com.example.SanjiBE.service.S3Service;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.net.URL;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/profile")
@Tag(name = "Profile", description = "프로필 사진 관련 API")

@RequiredArgsConstructor
public class UserProfileController {

    private final S3Service s3Service;
    private final UserRepository userRepository;

    // 프로필 업로드용 URL 발급
    @GetMapping("/upload-url")
    public ResponseEntity<GetS3Url> getUploadUrl(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam String filename,
            @RequestParam String contentType
    ) {
        String key = String.format("upload/profile/%d/%s_%s", user.getId(), UUID.randomUUID(), filename);
        URL presignedUrl = s3Service.createPresignedPutUrl(key, contentType);
        return ResponseEntity.ok(new GetS3Url(key, presignedUrl.toString()));
    }

    // 업로드 완료 후 DB 반영 (S3 업로드는 클라이언트에서 PUT으로 수행)
    @PostMapping("/save")
    public ResponseEntity<String> saveProfileKey(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam String key
    ) {
        Optional<User> userOpt = userRepository.findById(user.getId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User member = userOpt.get();
        member.setProfileKey(key);
        userRepository.save(member);
        return ResponseEntity.ok("프로필이 저장되었습니다.");
    }

    // 프로필 변경 (기존 S3 파일 삭제 + 새 key 저장)
    @PostMapping("/update")
    public ResponseEntity<String> updateProfile(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam String newKey
    ) {
        Optional<User> userOpt = userRepository.findById(user.getId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User member = userOpt.get();

        if (member.getProfileKey() != null) {
            s3Service.deleteObject(member.getProfileKey());
        }

        member.setProfileKey(newKey);
        userRepository.save(member);
        return ResponseEntity.ok("프로필이 업데이트 되었습니다.");
    }

    // 프로필 삭제
    @DeleteMapping("/delete")
    public ResponseEntity<String> deleteProfile(@AuthenticationPrincipal CustomUserDetails user) {
        Optional<User> userOpt = userRepository.findById(user.getId());
        if (userOpt.isEmpty()) {
            return ResponseEntity.badRequest().body("User not found");
        }

        User member = userOpt.get();
        if (member.getProfileKey() != null) {
            s3Service.deleteObject(member.getProfileKey());
            member.setProfileKey(null);
            userRepository.save(member);
        }

        return ResponseEntity.ok("프로필이 삭제되었습니다.");
    }
}
