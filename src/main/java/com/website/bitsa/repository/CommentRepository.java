package com.website.bitsa.repository;

import com.website.bitsa.model.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    // ========== POST QUERIES ==========

    /**
     * Find all approved comments for a post (Public View)
     * USED IN: BlogService.getPostComments
     */
    List<Comment> findByPostIdAndIsApprovedTrueOrderByCreatedAtDesc(Long postId);

    /**
     * Find all comments for a post (Admin View)
     */
    List<Comment> findByPostIdOrderByCreatedAtDesc(Long postId);

    /**
     * Count comments for a post
     */
    long countByPostIdAndIsApprovedTrue(Long postId);

    // ========== USER QUERIES ==========

    /**
     * Find all comments by user
     */
    List<Comment> findByUserIdOrderByCreatedAtDesc(Long userId);

    /**
     * Find approved comments by user
     */
    List<Comment> findByUserIdAndIsApprovedTrueOrderByCreatedAtDesc(Long userId);

    /**
     * Count comments by user
     */
    long countByUserId(Long userId);

    // ========== MODERATION QUERIES ==========

    /**
     * Find all pending comments (not approved, not flagged)
     * USED IN: BlogService.getPendingComments
     */
    List<Comment> findByIsApprovedFalseAndIsFlaggedFalseOrderByCreatedAtDesc();

    /**
     * Find all flagged comments
     */
    List<Comment> findByIsFlaggedTrueOrderByCreatedAtDesc();

    /**
     * Find all approved comments
     */
    List<Comment> findByIsApprovedTrueOrderByCreatedAtDesc();

    /**
     * Count pending comments
     */
    long countByIsApprovedFalseAndIsFlaggedFalse();

    /**
     * Count flagged comments
     */
    long countByIsFlaggedTrue();

    // ========== STATISTICS ==========

    /**
     * Find recent comments (last 10)
     */
    List<Comment> findTop10ByIsApprovedTrueOrderByCreatedAtDesc();

    /**
     * Find most liked comments
     */
    List<Comment> findTop10ByIsApprovedTrueOrderByLikeCountDesc();

    /**
     * Count total approved comments
     */
    long countByIsApprovedTrue();

    /**
     * Get total comments across all posts
     */
    @Query("SELECT COUNT(c) FROM Comment c WHERE c.isApproved = true")
    long countAllApprovedComments();
}