package com.example.blog.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String imageUrl;
    private int likeCount;
    private int commentCount;
    private String authorUsername;
    private String authorProfilePicture;
    private String authorEmail;
    private String createdAt;
    private String updatedAt;
    private List<UserDto> likers;
}
