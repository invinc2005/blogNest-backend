package com.example.blog.controller;

import com.example.blog.dto.NotificationDto;
import com.example.blog.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping("/me")
    public ResponseEntity<List<NotificationDto>> getMyNotifications(Authentication authentication) {
        List<NotificationDto> notifications = notificationService.getNotificationsForUser(authentication.getName());
        return ResponseEntity.ok(notifications);
    }
    @PostMapping("/{notificationId}/read")
    public ResponseEntity<Void> markNotificationAsRead(
            @PathVariable Long notificationId,
            Authentication authentication
    ) {
        notificationService.markNotificationAsRead(notificationId, authentication.getName());
        return ResponseEntity.ok().build();
    }
}