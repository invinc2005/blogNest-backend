package com.example.blog.service;

import com.example.blog.dto.CommentDto;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.entity.Role;
import com.example.blog.entity.User;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import com.example.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;
    private final UserRepository userRepository;

    @Transactional
    public CommentDto addComment(Long postId, String content, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new RuntimeException("Post not found"));
        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Comment comment = Comment.builder()
                .content(content)
                .post(post)
                .author(author)
                .build();

        Comment savedComment = commentRepository.save(comment);
        return convertToDto(savedComment);
    }

    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsForPost(Long postId) {
        List<Comment> comments = commentRepository.findByPostIdWithAuthor(postId);
        return comments.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public void deleteComment(Long commentId, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (currentUser.getRole() != Role.ADMIN && !comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to delete this comment");
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public CommentDto updateComment(Long commentId, String content, Authentication authentication) {
        User currentUser = (User) authentication.getPrincipal();
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new RuntimeException("Comment not found"));

        if (currentUser.getRole() != Role.ADMIN && !comment.getAuthor().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("You are not authorized to edit this comment");
        }
        comment.setEdited(true);
        comment.setUpdatedAt(LocalDateTime.now());
        comment.setContent(content);
        Comment updatedComment = commentRepository.save(comment);
        return convertToDto(updatedComment);
    }


    private CommentDto convertToDto(Comment comment) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return CommentDto.builder()
                .id(comment.getId())
                .content(comment.getContent())
                .authorUsername(comment.getAuthor().getDisplayName())
                .authorEmail(comment.getAuthor().getEmail())
                .authorProfilePicture(comment.getAuthor().getProfilePictureUrl())
                .createdAt(comment.getCreatedAt().format(formatter))
                .isEdited(comment.isEdited())
                .updatedAt(comment.getUpdatedAt() != null ? comment.getUpdatedAt().format(formatter) : null)
                .build();
    }
}