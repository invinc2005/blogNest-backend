package com.example.blog.service;

import com.example.blog.dto.NotificationDto;
import com.example.blog.entity.Notification;
import com.example.blog.entity.Post;
import com.example.blog.entity.User;
import com.example.blog.repository.NotificationRepository;
import com.example.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;

    public void createLikeNotification(User liker, Post post) {
        if (liker.getId().equals(post.getAuthor().getId())) {
            return;
        }
        String message = String.format("%s liked your post: '%s'", liker.getDisplayName(), post.getTitle());
        Notification notification = Notification.builder()
                .recipient(post.getAuthor())
                .message(message)
                .isRead(false)
                .build();
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationDto> getNotificationsForUser(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return notificationRepository.findByRecipientOrderByCreatedAtDesc(user).stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void markNotificationAsRead(Long notificationId, String userEmail) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found"));
        if (!notification.getRecipient().getEmail().equals(userEmail)) {
            throw new AccessDeniedException("You are not authorized to access this notification");
        }

        notificationRepository.markAsReadById(notificationId);
    }


    private NotificationDto convertToDto(Notification notification) {
        return NotificationDto.builder()
                .id(notification.getId())
                .message(notification.getMessage())
                .isRead(notification.isRead())
                .createdAt(notification.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")))
                .build();
    }
}