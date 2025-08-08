package com.example.blog.dto;

import lombok.Builder;
import lombok.Data;


@Data
@Builder
public class CommentDto {
    private Long id;
    private String content;
    private String createdAt;
    private String authorUsername;
    private String authorEmail;
    private String authorProfilePicture;
    private boolean isEdited;
    private String updatedAt;
}