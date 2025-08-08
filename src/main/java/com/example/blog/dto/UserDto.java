package com.example.blog.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserDto {
    private Long id;
    private String displayName;
    private String email;
    private String profilePictureUrl;
}