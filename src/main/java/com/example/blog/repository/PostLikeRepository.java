package com.example.blog.repository;

import com.example.blog.entity.Post;
import com.example.blog.entity.PostLike;
import com.example.blog.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PostLikeRepository extends JpaRepository<PostLike, Long> {
    Optional<PostLike> findByUserAndPost(User user, Post post);
    List<PostLike> findAllByUser(User user);
}