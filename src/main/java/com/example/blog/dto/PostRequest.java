package com.example.blog.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
public class PostRequest {
    private String title;
    private String content;
    private String imageUrl;
    private MultipartFile imageFile;

    public PostRequest(String title, String content, String imageUrl, MultipartFile imageFile) {
        this.title = title;
        this.content = content;
        this.imageUrl = imageUrl;
        this.imageFile = imageFile;
    }
}
