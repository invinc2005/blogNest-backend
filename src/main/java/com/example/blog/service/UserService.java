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
    private final FileStorageService fileStorageService;
    private final PostRepository postRepository;

    public List<MonthlyPostCountDto> getMonthlyPostCounts(Long authorId) {
        List<MonthlyPostCountDto> monthlyStats = new ArrayList<>();
        YearMonth currentMonth = YearMonth.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM");

        for (int i = 5; i >= 0; i--) {
            YearMonth targetMonth = currentMonth.minusMonths(i);
            monthlyStats.add(new MonthlyPostCountDto(targetMonth.format(formatter), 0L));
        }

        LocalDateTime startDate = currentMonth.minusMonths(5).atDay(1).atStartOfDay();
        List<Object[]> postCounts = postRepository.countByAuthorAndMonth(authorId, startDate);

        Map<String, Long> dbCounts = postCounts.stream()
                .collect(Collectors.toMap(
                        obj -> (String) obj[0],
                        obj -> (Long) obj[1]
                ));

        monthlyStats.forEach(dto -> {
            if (dbCounts.containsKey(dto.getMonth())) {
                dto.setPostCount(dbCounts.get(dto.getMonth()));
            }
        });

        return monthlyStats;
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

        String filename = fileStorageService.store(file);
        String fileUrl = "/uploads/" + filename;

        user.setProfilePictureUrl(fileUrl);
        userRepository.save(user);

        return UserDto.builder()
                .id(user.getId())
                .displayName(user.getDisplayName()) // <-- Use the new getter
                .email(user.getEmail())
                .profilePictureUrl(user.getProfilePictureUrl())
                .build();
    }
}