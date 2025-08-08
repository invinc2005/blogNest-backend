package com.example.blog.controller;

import com.example.blog.dto.PostResponse;
import com.example.blog.dto.UserDto;
import com.example.blog.dto.UsernameUpdateRequest;
import com.example.blog.entity.User;
import com.example.blog.service.PostService;
import com.example.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final PostService postService;


    @GetMapping("/me")
    public ResponseEntity<UserDto> getCurrentUser(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();

        UserDto userDto = UserDto.builder()
                .id(currentUser.getId())
                .displayName(currentUser.getDisplayName()) // <-- Use getDisplayName()
                .email(currentUser.getEmail())
                .profilePictureUrl(currentUser.getProfilePictureUrl())
                .build();

        return ResponseEntity.ok(userDto);
    }
    @PutMapping("/me/username")
    public ResponseEntity<UserDto> updateMyUsername(@RequestBody Map<String, String> payload, Authentication authentication) {
        String newDisplayName = payload.get("displayName");
        UserDto updatedUser = userService.updateDisplayName(authentication.getName(), newDisplayName);
        return ResponseEntity.ok(updatedUser);
    }

    @GetMapping("/me/liked-posts")
    public ResponseEntity<List<PostResponse>> getMyLikedPosts(Authentication authentication) {
        List<PostResponse> likedPosts = postService.getLikedPosts(authentication.getName());
        return ResponseEntity.ok(likedPosts);
    }

    @PostMapping(value = "/me/profile-picture", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<UserDto> uploadProfilePicture(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {
        UserDto updatedUser = userService.updateProfilePicture(authentication.getName(), file);
        return ResponseEntity.ok(updatedUser);
    }
}