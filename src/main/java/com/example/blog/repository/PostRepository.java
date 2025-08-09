package com.example.blog.repository;

import com.example.blog.entity.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface PostRepository extends JpaRepository<Post, Long> {

    @EntityGraph(attributePaths = {"author"})
    List<Post> findAllByAuthorId(Long authorId);

    @Override
    @EntityGraph(attributePaths = {"author"})
    Page<Post> findAll(Pageable pageable);

    @EntityGraph(attributePaths = {"author"})
    Page<Post> findAllByOrderByLikeCountDesc(Pageable pageable);

    @Query("SELECT FUNCTION('DATE_FORMAT', p.createdAt, '%Y-%m'), COUNT(p) " +
            "FROM Post p WHERE p.author.id = :authorId AND p.createdAt >= :startDate " +
            "GROUP BY FUNCTION('DATE_FORMAT', p.createdAt, '%Y-%m')")
    List<Object[]> countByAuthorAndMonth(@Param("authorId") Long authorId, @Param("startDate") LocalDateTime startDate);
    @Query("SELECT p.createdAt FROM Post p WHERE p.author.id = :authorId AND p.createdAt >= :startDate")
    List<LocalDateTime> findCreatedAtByAuthorSince(@Param("authorId") Long authorId, @Param("startDate") LocalDateTime startDate);
}