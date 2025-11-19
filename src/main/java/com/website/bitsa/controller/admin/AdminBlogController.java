package com.website.bitsa.controller.admin;

import com.website.bitsa.dto.request.CreatePostRequest;
import com.website.bitsa.dto.response.ApiResponse;
import com.website.bitsa.dto.response.CommentResponse; // <-- Ensure this is imported
import com.website.bitsa.dto.response.PostResponse;
import com.website.bitsa.model.Comment;
import com.website.bitsa.service.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/blog")
@RequiredArgsConstructor
// No @CrossOrigin here (SecurityConfig handles it)
public class AdminBlogController {

    private final BlogService blogService;

    @PostMapping("/posts")
    public ResponseEntity<ApiResponse> createPost(
            @Valid @RequestBody CreatePostRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();
        PostResponse post = blogService.createPost(request, userEmail);

        return new ResponseEntity<>(
                ApiResponse.success("Post created successfully!", post),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/posts/{id}")
    public ResponseEntity<ApiResponse> updatePost(
            @PathVariable Long id,
            @Valid @RequestBody CreatePostRequest request) {

        PostResponse post = blogService.updatePost(id, request);
        return ResponseEntity.ok(ApiResponse.success("Post updated successfully!", post));
    }

    @DeleteMapping("/posts/{id}")
    public ResponseEntity<ApiResponse> deletePost(@PathVariable Long id) {
        blogService.deletePost(id);
        return ResponseEntity.ok(ApiResponse.success("Post deleted successfully!"));
    }

    @PutMapping("/posts/{id}/toggle-publish")
    public ResponseEntity<ApiResponse> togglePublish(@PathVariable Long id) {
        PostResponse post = blogService.togglePublish(id);
        return ResponseEntity.ok(ApiResponse.success("Post publish status updated!", post));
    }

    // --- ENDPOINTS for Admin Panel Fetching ---

    @GetMapping("/posts/{id}")
    public ResponseEntity<PostResponse> getPostById(@PathVariable Long id) {
        PostResponse post = blogService.getPostById(id);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/all-posts")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> posts = blogService.getAllPostsAdmin();
        return ResponseEntity.ok(posts);
    }

    // --- COMMENT MANAGEMENT ---

    @GetMapping("/comments/pending")
    public ResponseEntity<List<CommentResponse>> getPendingComments() {
        // Returns DTOs (Safe)
        List<CommentResponse> comments = blogService.getPendingComments();
        return ResponseEntity.ok(comments);
    }

    @PutMapping("/comments/{id}/approve")
    public ResponseEntity<ApiResponse> approveComment(
            @PathVariable Long id,
            Authentication authentication) {

        // 1. Approve logic (Returns Entity)
        // In a real app, you'd get the admin ID from 'authentication'
        Comment comment = blogService.approveComment(id, 1L);

        // 2. Convert to DTO (THIS IS THE FIX)
        // This prevents the Infinite Loop / 500 Error
        CommentResponse responseDto = CommentResponse.builder()
                .id(comment.getId())
                .commentText(comment.getCommentText())
                .userName(comment.getUser().getName())
                .createdAt(comment.getCreatedAt())
                .build();

        return ResponseEntity.ok(ApiResponse.success("Comment approved!", responseDto));
    }

    @DeleteMapping("/comments/{id}")
    public ResponseEntity<ApiResponse> deleteComment(@PathVariable Long id) {
        blogService.deleteComment(id);
        return ResponseEntity.ok(ApiResponse.success("Comment deleted successfully!"));
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Admin Blog API is working!");
    }
}