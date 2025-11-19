package com.website.bitsa.repository;

import com.website.bitsa.model.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    // ========== PUBLISHING QUERIES ==========

    List<Post> findByIsPublishedTrueOrderByPublishedAtDesc();

    Page<Post> findByIsPublishedTrue(Pageable pageable);

    List<Post> findByIsPublishedFalseOrderByCreatedAtDesc();

    Optional<Post> findBySlugAndIsPublishedTrue(String slug);

    // ========== FEATURED & PINNED ==========

    List<Post> findByIsFeaturedTrueAndIsPublishedTrueOrderByPublishedAtDesc();

    List<Post> findByIsPinnedTrueAndIsPublishedTrueOrderByPublishedAtDesc();

    // ========== CATEGORY QUERIES (FIXED) ==========

    @Query("SELECT p FROM Post p WHERE p.category.id = :categoryId AND p.isPublished = true ORDER BY p.publishedAt DESC")
    List<Post> findByCategoryIdAndIsPublishedTrueOrderByPublishedAtDesc(@Param("categoryId") Long categoryId);

    @Query("SELECT p FROM Post p WHERE p.category.id = :categoryId AND p.isPublished = true")
    Page<Post> findByCategoryIdAndIsPublishedTrue(@Param("categoryId") Long categoryId, Pageable pageable);

    @Query("SELECT COUNT(p) FROM Post p WHERE p.category.id = :categoryId AND p.isPublished = true")
    long countByCategoryIdAndIsPublishedTrue(@Param("categoryId") Long categoryId);

    // ========== AUTHOR QUERIES ==========

    List<Post> findByAuthorIdOrderByCreatedAtDesc(Long authorId);

    List<Post> findByAuthorIdAndIsPublishedTrueOrderByPublishedAtDesc(Long authorId);

    long countByAuthorId(Long authorId);

    // ========== SEARCH QUERIES ==========

    List<Post> findByTitleContainingIgnoreCaseAndIsPublishedTrue(String keyword);

    @Query("SELECT p FROM Post p WHERE p.isPublished = true AND " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    List<Post> searchPosts(@Param("keyword") String keyword);

    @Query("SELECT p FROM Post p WHERE p.isPublished = true AND " +
            "(LOWER(p.title) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.content) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Post> searchPosts(@Param("keyword") String keyword, Pageable pageable);

    // ========== TAGS QUERIES ==========

    @Query("SELECT p FROM Post p WHERE p.isPublished = true AND p.tags LIKE %:tag%")
    List<Post> findByTag(@Param("tag") String tag);

    // ========== STATISTICS & ANALYTICS ==========

    List<Post> findTop10ByIsPublishedTrueOrderByViewCountDesc();

    List<Post> findTop10ByIsPublishedTrueOrderByLikeCountDesc();

    List<Post> findTop10ByIsPublishedTrueOrderByCommentCountDesc();

    @Query("SELECT p FROM Post p WHERE p.isPublished = true AND p.publishedAt > :date ORDER BY p.publishedAt DESC")
    List<Post> findRecentPosts(@Param("date") LocalDateTime date);

    long countByIsPublishedTrue();

    @Query("SELECT SUM(p.viewCount) FROM Post p WHERE p.isPublished = true")
    Long getTotalViews();

    // ========== RELATED POSTS (FIXED) ==========

    @Query("SELECT p FROM Post p WHERE p.category.id = :categoryId AND p.id != :postId AND p.isPublished = true ORDER BY p.publishedAt DESC")
    List<Post> findRelatedPosts(@Param("categoryId") Long categoryId, @Param("postId") Long postId, Pageable pageable);
    List<Post> findAllByOrderByCreatedAtDesc();
}