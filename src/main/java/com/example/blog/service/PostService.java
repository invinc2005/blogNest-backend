    package com.example.blog.service;

    import com.example.blog.dto.PostRequest;
    import com.example.blog.dto.PostResponse;
    import com.example.blog.dto.UserDto;
    import com.example.blog.entity.Post;
    import com.example.blog.entity.PostLike;
    import com.example.blog.entity.User;
    import com.example.blog.repository.PostLikeRepository;
    import com.example.blog.repository.PostRepository;
    import com.example.blog.repository.UserRepository;
    import lombok.RequiredArgsConstructor;
    import org.springframework.data.domain.Page;
    import org.springframework.data.domain.Pageable;
    import org.springframework.security.access.AccessDeniedException;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.io.IOException;
    import java.util.ArrayList; // <-- Import ArrayList
    import java.time.LocalDateTime;
    import java.time.format.DateTimeFormatter;
    import java.util.ArrayList;
    import java.util.HashSet;
    import java.util.List;
    import java.util.stream.Collectors;

    @Service
    @RequiredArgsConstructor
    public class PostService {
        private final NotificationService notificationService;
        private final PostRepository postRepository;
        private final UserRepository userRepository;
        private final PostLikeRepository postLikeRepository;
        private final StorageService storageService;

        @Transactional
        public PostResponse createPost(PostRequest request, String userEmail) {
            User author = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            String finalImageUrl = request.getImageUrl();
            if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
                try {
                    // Upload to Supabase
                    finalImageUrl = storageService.uploadFile(request.getImageFile());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload post image", e);
                }
            }

            Post post = Post.builder()
                    .title(request.getTitle())
                    .content(request.getContent())
                    .imageUrl(finalImageUrl)
                    .author(author)
                    .build();
            if (post.getComments() == null) {
                post.setComments(new HashSet<>());
            }
            if (post.getLikes() == null) {
                post.setLikes(new HashSet<>());
            }
            Post savedPost = postRepository.save(post);
            return convertToDto(savedPost);
        }

        @Transactional(readOnly = true)
        public Page<PostResponse> getAllPosts(Pageable pageable) {
            Page<Post> postsPage = postRepository.findAll(pageable);
            return postsPage.map(this::convertToDto);
        }

        @Transactional(readOnly = true)
        public List<PostResponse> getUserPosts(String userEmail) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<Post> posts = postRepository.findAllByAuthorId(user.getId());
            return posts.stream()
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }

        @Transactional
        public void deletePost(Long postId, String userEmail) {
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            if (!post.getAuthor().getEmail().equals(userEmail)) {
                throw new AccessDeniedException("You are not authorized to delete this post");
            }

            postRepository.delete(post);
        }

        @Transactional
        public PostResponse updatePost(Long id, PostRequest request, String userEmail) {
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

            if (!post.getAuthor().getEmail().equals(userEmail)) {
                throw new AccessDeniedException("You are not authorized to edit this post");
            }

            String finalImageUrl = request.getImageUrl();
            if (request.getImageFile() != null && !request.getImageFile().isEmpty()) {
                try {
                    // Upload to Supabase
                    finalImageUrl = storageService.uploadFile(request.getImageFile());
                } catch (IOException e) {
                    throw new RuntimeException("Failed to upload post image", e);
                }
            }

            post.setTitle(request.getTitle());
            post.setContent(request.getContent());
            post.setImageUrl(finalImageUrl);
            post.setUpdatedAt(LocalDateTime.now());

            if (post.getComments() == null) {
                post.setComments(new HashSet<>());
            }
            if (post.getLikes() == null) {
                post.setLikes(new HashSet<>());
            }
            Post updatedPost = postRepository.save(post);
            return convertToDto(updatedPost);
        }

        @Transactional(readOnly = true)
        public PostResponse getPostById(Long id) {
            Post post = postRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Post not found with id: " + id));

            PostResponse response = convertToDto(post);

            List<UserDto> likers = post.getLikes().stream()
                    .map(PostLike::getUser)
                    .limit(4)
                    .map(user -> UserDto.builder()
                            .displayName(user.getDisplayName())
                            .profilePictureUrl(user.getProfilePictureUrl())
                            .build())
                    .collect(Collectors.toList());

            response.setLikers(likers);
            return response;
        }


        private PostResponse convertToDto(Post post) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

            if (post.getAuthor() == null) {
                return null;
            }

            int commentCount = (post.getComments() == null) ? 0 : post.getComments().size();

            return new PostResponse(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getImageUrl(),
                    post.getLikeCount(),
                    commentCount,
                    post.getAuthor().getDisplayName(),
                    post.getAuthor().getProfilePictureUrl(),
                    post.getAuthor().getEmail(),
                    post.getCreatedAt() != null ? post.getCreatedAt().format(formatter) : null,
                    post.getUpdatedAt() != null ? post.getUpdatedAt().format(formatter) : null,
                    new ArrayList<>() // <-- ADD THIS MISSING ARGUMENT FOR THE 'likers' LIST
            );
        }
        @Transactional
        public void likePost(Long postId, String userEmail) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            if (postLikeRepository.findByUserAndPost(user, post).isPresent()) {
                throw new IllegalStateException("You have already liked this post");
            }

            PostLike like = PostLike.builder().user(user).post(post).build();
            postLikeRepository.save(like);

            post.setLikeCount(post.getLikeCount() + 1);
            postRepository.save(post);


            notificationService.createLikeNotification(user, post);
        }

        @Transactional
        public void unlikePost(Long postId, String userEmail) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new RuntimeException("Post not found"));

            PostLike like = postLikeRepository.findByUserAndPost(user, post)
                    .orElseThrow(() -> new IllegalStateException("You have not liked this post"));

            postLikeRepository.delete(like);

            post.setLikeCount(Math.max(0, post.getLikeCount() - 1));
            postRepository.save(post);
        }
        @Transactional(readOnly = true)
        public List<PostResponse> getLikedPosts(String userEmail) {
            User user = userRepository.findByEmail(userEmail)
                    .orElseThrow(() -> new RuntimeException("User not found"));

            List<PostLike> userLikes = postLikeRepository.findAllByUser(user);

            return userLikes.stream()
                    .map(PostLike::getPost)
                    .map(this::convertToDto)
                    .collect(Collectors.toList());
        }
        @Transactional(readOnly = true)
        public Page<PostResponse> getTrendingPosts(Pageable pageable) {
            Page<Post> trendingPostsPage = postRepository.findAllByOrderByLikeCountDesc(pageable);
            return trendingPostsPage.map(this::convertToDto);
        }
    }