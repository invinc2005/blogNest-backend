package com.example.blog.controller;

import com.example.blog.dto.CommentDto;
import com.example.blog.dto.CommentRequest;
import com.example.blog.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    @PostMapping
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long postId,
            @RequestBody CommentRequest commentRequest,
            Authentication authentication
    ) {
        CommentDto createdComment = commentService.addComment(
                postId,
                commentRequest.getContent(),
                authentication.getName()
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(createdComment);
    }

    @GetMapping
    public ResponseEntity<List<CommentDto>> getCommentsForPost(@PathVariable Long postId) {
        return ResponseEntity.ok(commentService.getCommentsForPost(postId));
    }

    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            Authentication authentication // Spring will provide this
    ) {
        commentService.deleteComment(commentId, authentication);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long postId,
            @PathVariable Long commentId,
            @RequestBody CommentRequest commentRequest,
            Authentication authentication // Spring will provide this
    ) {
        CommentDto updatedComment = commentService.updateComment(
                commentId,
                commentRequest.getContent(),
                authentication
        );
        return ResponseEntity.ok(updatedComment);
    }
}