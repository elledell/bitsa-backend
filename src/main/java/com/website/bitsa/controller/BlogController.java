package com.website.bitsa.controller;

import com.website.bitsa.dto.request.CreateCommentRequest;
import com.website.bitsa.dto.response.ApiResponse;
import com.website.bitsa.dto.response.CommentResponse; // <-- IMPORT THIS
import com.website.bitsa.dto.response.PostResponse;
import com.website.bitsa.model.Comment;
import com.website.bitsa.service.BlogService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/blog")
@RequiredArgsConstructor
// Ensure this matches your React URL
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class BlogController {

    private final BlogService blogService;

    @GetMapping("/posts")
    public ResponseEntity<List<PostResponse>> getAllPosts() {
        List<PostResponse> posts = blogService.getAllPublishedPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/{slug}")
    public ResponseEntity<PostResponse> getPostBySlug(@PathVariable String slug) {
        PostResponse post = blogService.getPostBySlug(slug);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/posts/category/{categoryId}")
    public ResponseEntity<List<PostResponse>> getPostsByCategory(@PathVariable Long categoryId) {
        List<PostResponse> posts = blogService.getPostsByCategory(categoryId);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/search")
    public ResponseEntity<List<PostResponse>> searchPosts(@RequestParam String keyword) {
        List<PostResponse> posts = blogService.searchPosts(keyword);
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/posts/featured")
    public ResponseEntity<List<PostResponse>> getFeaturedPosts() {
        List<PostResponse> posts = blogService.getFeaturedPosts();
        return ResponseEntity.ok(posts);
    }

    // --- THIS IS THE FIX: Return List<CommentResponse> instead of List<Comment> ---
    @GetMapping("/posts/{postId}/comments")
    public ResponseEntity<List<CommentResponse>> getPostComments(@PathVariable Long postId) {
        // The service now returns CommentResponse objects, which are safe (no infinite loop)
        List<CommentResponse> comments = blogService.getPostComments(postId);
        return ResponseEntity.ok(comments);
    }

    // --- THIS IS THE FIX: Return CommentResponse inside ApiResponse ---
    @PostMapping("/posts/{postId}/comments")
    public ResponseEntity<ApiResponse> addComment(
            @PathVariable Long postId,
            @Valid @RequestBody CreateCommentRequest request,
            Authentication authentication) {

        String userEmail = authentication.getName();

        // 1. Call service (returns raw Comment entity)
        Comment comment = blogService.addComment(postId, request, userEmail);

        // 2. Convert to DTO immediately to avoid infinite loop in response
        CommentResponse responseDto = CommentResponse.builder()
                .id(comment.getId())
                .commentText(comment.getCommentText())
                .userName(comment.getUser().getName())
                .createdAt(comment.getCreatedAt())
                .build();

        return new ResponseEntity<>(
                ApiResponse.success("Comment submitted!", responseDto),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Blog API is working!");
    }
}