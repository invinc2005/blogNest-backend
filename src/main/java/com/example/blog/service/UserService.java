package com.example.blog.service;

import com.example.blog.dto.MonthlyPostCountDto;
import com.example.blog.dto.UserDto;
import com.example.blog.entity.User;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PostRepository postRepository;
    private final StorageService storageService;

    public List<MonthlyPostCountDto> getMonthlyPostCounts(Long authorId) {
        LocalDateTime startDate = LocalDateTime.now().minusMonths(6);

        // 1. Fetch the raw list of timestamps from the simple query
        List<LocalDateTime> timestamps = postRepository.findCreatedAtByAuthorSince(authorId, startDate);

        // 2. Process the data in Java using Streams
        Map<String, Long> monthlyCounts = timestamps.stream()
                .collect(Collectors.groupingBy(
                        dt -> dt.format(DateTimeFormatter.ofPattern("yyyy-MM")),
                        Collectors.counting()
                ));

        // 3. Convert the map to the DTO list
        return monthlyCounts.entrySet().stream()
                .map(entry -> new MonthlyPostCountDto(entry.getKey(), entry.getValue()))
                .sorted((a, b) -> b.getMonth().compareTo(a.getMonth())) // Sort newest first
                .collect(Collectors.toList());
    }

    public User getByEmail(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
    }
    public boolean isDisplayNameTaken(String displayName) {
        return userRepository.existsByDisplayName(displayName);
    }


    @Transactional
    public UserDto updateDisplayName(String userEmail, String newDisplayName) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (userRepository.existsByDisplayName(newDisplayName)) {
            throw new IllegalArgumentException("Username is already taken.");
        }

        user.setDisplayName(newDisplayName);
        userRepository.save(user);

        return UserDto.builder()
                .id(user.getId())
                .displayName(user.getDisplayName())
                .email(user.getEmail())
                .profilePictureUrl(user.getProfilePictureUrl())
                .build();
    }
    @Transactional
    public UserDto updateProfilePicture(String userEmail, MultipartFile file) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        try {
            // Upload the file to Supabase and get the full public URL
            String fileUrl = storageService.uploadFile(file);
            user.setProfilePictureUrl(fileUrl);
            userRepository.save(user);

            return UserDto.builder()
                    .id(user.getId())
                    .displayName(user.getDisplayName())
                    .email(user.getEmail())
                    .profilePictureUrl(user.getProfilePictureUrl())
                    .build();
        } catch (IOException e) {
            // In a real app, handle this more gracefully
            throw new RuntimeException("Failed to upload profile picture", e);
        }
    }
}