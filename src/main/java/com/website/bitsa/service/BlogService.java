package com.website.bitsa.service;

import com.website.bitsa.dto.request.CreateCommentRequest;
import com.website.bitsa.dto.request.CreatePostRequest;
import com.website.bitsa.dto.response.CommentResponse;
import com.website.bitsa.dto.response.PostResponse;
import com.website.bitsa.exception.BadRequestException;
import com.website.bitsa.exception.ResourceNotFoundException;
import com.website.bitsa.model.Category;
import com.website.bitsa.model.Comment;
import com.website.bitsa.model.Post;
import com.website.bitsa.model.User;
import com.website.bitsa.repository.CategoryRepository;
import com.website.bitsa.repository.CommentRepository;
import com.website.bitsa.repository.PostRepository;
import com.website.bitsa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BlogService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    // ========== ADMIN POST OPERATIONS ==========

    public List<PostResponse> getAllPostsAdmin() {
        return postRepository.findAllByOrderByCreatedAtDesc()
                .stream()
                .map(this::convertToPostResponse)
                .collect(Collectors.toList());
    }

    // ========== PUBLIC POST OPERATIONS ==========

    public List<PostResponse> getAllPublishedPosts() {
        return postRepository.findByIsPublishedTrueOrderByPublishedAtDesc()
                .stream()
                .map(this::convertToPostResponse)
                .collect(Collectors.toList());
    }

    public PostResponse getPostBySlug(String slug) {
        Post post = postRepository.findBySlugAndIsPublishedTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with slug: " + slug));

        post.incrementViewCount();
        postRepository.save(post);

        return convertToPostResponse(post);
    }

    public PostResponse getPostById(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found with id: " + id));
        return convertToPostResponse(post);
    }

    public List<PostResponse> getPostsByCategory(Long categoryId) {
        return postRepository.findByCategoryIdAndIsPublishedTrueOrderByPublishedAtDesc(categoryId)
                .stream()
                .map(this::convertToPostResponse)
                .collect(Collectors.toList());
    }

    public List<PostResponse> searchPosts(String keyword) {
        return postRepository.searchPosts(keyword)
                .stream()
                .map(this::convertToPostResponse)
                .collect(Collectors.toList());
    }

    public List<PostResponse> getFeaturedPosts() {
        return postRepository.findByIsFeaturedTrueAndIsPublishedTrueOrderByPublishedAtDesc()
                .stream()
                .map(this::convertToPostResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public PostResponse createPost(CreatePostRequest request, String userEmail) {
        User author = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        Post post = Post.builder()
                .title(request.getTitle())
                .content(request.getContent())
                .excerpt(request.getExcerpt())
                .author(author)
                .category(category)
                .featuredImage(request.getFeaturedImage())
                .featuredImageAlt(request.getFeaturedImageAlt())
                .metaTitle(request.getMetaTitle())
                .metaDescription(request.getMetaDescription())
                .metaKeywords(request.getMetaKeywords())
                .tags(request.getTags())
                .isPublished(request.getIsPublished())
                .isFeatured(request.getIsFeatured())
                .commentsEnabled(request.getCommentsEnabled())
                .build();

        if (request.getIsPublished()) {
            post.publish();
        }

        Post savedPost = postRepository.save(post);

        // Update category post count
        category.incrementPostCount();
        categoryRepository.save(category);

        return convertToPostResponse(savedPost);
    }

    @Transactional
    public PostResponse updatePost(Long id, CreatePostRequest request) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found"));

        post.setTitle(request.getTitle());
        post.setContent(request.getContent());
        post.setExcerpt(request.getExcerpt());
        post.setCategory(category);
        post.setFeaturedImage(request.getFeaturedImage());
        post.setFeaturedImageAlt(request.getFeaturedImageAlt());
        post.setMetaTitle(request.getMetaTitle());
        post.setMetaDescription(request.getMetaDescription());
        post.setMetaKeywords(request.getMetaKeywords());
        post.setTags(request.getTags());
        post.setIsFeatured(request.getIsFeatured());
        post.setCommentsEnabled(request.getCommentsEnabled());

        if (request.getIsPublished() && !post.getIsPublished()) {
            post.publish();
        } else if (!request.getIsPublished() && post.getIsPublished()) {
            post.unpublish();
        }

        Post updatedPost = postRepository.save(post);
        return convertToPostResponse(updatedPost);
    }

    public void deletePost(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        // Decrement category post count
        Category category = post.getCategory();
        category.decrementPostCount();
        categoryRepository.save(category);

        postRepository.delete(post);
    }

    public PostResponse togglePublish(Long id) {
        Post post = postRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (post.getIsPublished()) {
            post.unpublish();
        } else {
            post.publish();
        }

        Post updatedPost = postRepository.save(post);
        return convertToPostResponse(updatedPost);
    }

    // ========== COMMENT OPERATIONS ==========

    public List<CommentResponse> getPostComments(Long postId) {
        return commentRepository.findByPostIdAndIsApprovedTrueOrderByCreatedAtDesc(postId)
                .stream()
                .map(comment -> CommentResponse.builder()
                        .id(comment.getId())
                        .commentText(comment.getCommentText())
                        .userName(comment.getUser().getName())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    public Comment addComment(Long postId, CreateCommentRequest request, String userEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new ResourceNotFoundException("Post not found"));

        if (!post.getCommentsEnabled()) {
            throw new BadRequestException("Comments are disabled for this post");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Comment comment = Comment.builder()
                .commentText(request.getCommentText())
                .post(post)
                .user(user)
                // --- CHANGED BACK TO FALSE (Admin must approve) ---
                .isApproved(false)
                .build();

        Comment savedComment = commentRepository.save(comment);

        // Update post comment count
        post.incrementCommentCount();
        postRepository.save(post);

        return savedComment;
    }

    public Comment approveComment(Long commentId, Long adminId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        comment.approve(adminId);
        return commentRepository.save(comment);
    }

    public void deleteComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new ResourceNotFoundException("Comment not found"));

        // Decrement post comment count
        Post post = comment.getPost();
        post.decrementCommentCount();
        postRepository.save(post);

        commentRepository.delete(comment);
    }

    public List<CommentResponse> getPendingComments() {
        return commentRepository.findByIsApprovedFalseAndIsFlaggedFalseOrderByCreatedAtDesc()
                .stream()
                .map(comment -> CommentResponse.builder()
                        .id(comment.getId())
                        .commentText(comment.getCommentText())
                        .userName(comment.getUser().getName())
                        .createdAt(comment.getCreatedAt())
                        .build())
                .collect(Collectors.toList());
    }

    // ========== HELPER METHODS ==========

    private PostResponse convertToPostResponse(Post post) {
        return PostResponse.builder()
                .id(post.getId())
                .title(post.getTitle())
                .content(post.getContent())
                .excerpt(post.getExcerpt())
                .slug(post.getSlug())
                .authorName(post.getAuthor().getName())
                .categoryName(post.getCategory().getName())
                .featuredImage(post.getFeaturedImage())
                .isPublished(post.getIsPublished())
                .isFeatured(post.getIsFeatured())
                .viewCount(post.getViewCount())
                .likeCount(post.getLikeCount())
                .commentCount(post.getCommentCount())
                .readingTimeMinutes(post.getReadingTimeMinutes())
                .tags(post.getTagsArray())
                .publishedAt(post.getPublishedAt())
                .createdAt(post.getCreatedAt())
                .build();
    }

    public long countTotalPosts() {
        return postRepository.countByIsPublishedTrue();
    }
}