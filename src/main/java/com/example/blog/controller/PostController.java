package com.example.blog.controller;

import com.example.blog.dto.MonthlyPostCountDto;
import com.example.blog.dto.PostRequest;
import com.example.blog.dto.PostResponse;
import com.example.blog.entity.User;
import com.example.blog.service.PostService;
import com.example.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService; // Inject the service via the constructor
    private final UserService userService;

    @GetMapping("/me/post-stats")
    public ResponseEntity<List<MonthlyPostCountDto>> getMyPostStats(Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        return ResponseEntity.ok(userService.getMonthlyPostCounts(currentUser.getId()));
    }


    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> createPost(
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        // We'll create a new DTO to pass this data to the service
        PostRequest postRequest = new PostRequest(title, content, imageUrl, imageFile);
        PostResponse createdPost = postService.createPost(postRequest, userEmail);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPost);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        PostResponse post = postService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    @GetMapping
    public ResponseEntity<  Page<PostResponse>> getAllPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getAllPosts(pageable));
    }

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<PostResponse> updatePost(
            @PathVariable Long id,
            @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam(value = "imageUrl", required = false) String imageUrl,
            @RequestParam(value = "imageFile", required = false) MultipartFile imageFile,
            Authentication authentication
    ) {
        String userEmail = authentication.getName();
        PostRequest postRequest = new PostRequest(title, content, imageUrl, imageFile);
        PostResponse updatedPost = postService.updatePost(id, postRequest, userEmail);
        return ResponseEntity.ok(updatedPost);
    }

    @GetMapping("/me")
    public ResponseEntity<List<PostResponse>> getMyPosts(Authentication authentication) {
        String userEmail = authentication.getName();
        return ResponseEntity.ok(postService.getUserPosts(userEmail));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id, Authentication authentication) {
        String userEmail = authentication.getName();
        postService.deletePost(id, userEmail);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/{postId}/like")
    public ResponseEntity<Void> likePost(@PathVariable Long postId, Authentication authentication) {
        postService.likePost(postId, authentication.getName());
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{postId}/like")
    public ResponseEntity<Void> unlikePost(@PathVariable Long postId, Authentication authentication) {
        postService.unlikePost(postId, authentication.getName());
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/me/liked-posts")
    public ResponseEntity<List<PostResponse>> getMyLikedPosts(Authentication authentication) {
        List<PostResponse> likedPosts = postService.getLikedPosts(authentication.getName());
        return ResponseEntity.ok(likedPosts);
    }

    @GetMapping("/trending")
    public ResponseEntity<Page<PostResponse>> getTrendingPosts(Pageable pageable) {
        return ResponseEntity.ok(postService.getTrendingPosts(pageable));
    }
}