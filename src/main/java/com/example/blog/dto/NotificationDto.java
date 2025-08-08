package com.example.blog.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class NotificationDto {
    private Long id;
    private String message;
    private boolean isRead;
    private String createdAt;
}