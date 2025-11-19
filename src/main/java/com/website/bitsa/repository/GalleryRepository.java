package com.website.bitsa.repository;


import com.website.bitsa.model.Gallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GalleryRepository extends JpaRepository<Gallery, Long> {

    // ========== PUBLISHING QUERIES ==========

    /**
     * Find all published images
     */
    List<Gallery> findByIsPublishedTrueOrderByCreatedAtDesc();

    /**
     * Find published images ordered by display order
     */
    List<Gallery> findByIsPublishedTrueOrderByDisplayOrderAsc();

    /**
     * Find featured images
     */
    List<Gallery> findByIsFeaturedTrueAndIsPublishedTrueOrderByDisplayOrderAsc();

    // ========== CATEGORY QUERIES ==========

    /**
     * Find images by category
     */
    List<Gallery> findByCategoryAndIsPublishedTrueOrderByCreatedAtDesc(String category);

    /**
     * Find all categories (distinct)
     */
    @Query("SELECT DISTINCT g.category FROM Gallery g WHERE g.isPublished = true AND g.category IS NOT NULL")
    List<String> findAllCategories();

    // ========== UPLOADER QUERIES ==========

    /**
     * Find images uploaded by user
     */
    List<Gallery> findByUploadedByIdOrderByCreatedAtDesc(Long userId);

    /**
     * Count images by uploader
     */
    long countByUploadedById(Long userId);

    // ========== EVENT DATE QUERIES ==========

    /**
     * Find images by event date range
     */
    List<Gallery> findByEventDateBetweenAndIsPublishedTrueOrderByEventDateDesc(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find recent event images
     */
    List<Gallery> findByEventDateAfterAndIsPublishedTrueOrderByEventDateDesc(LocalDateTime date);

    // ========== STATISTICS ==========

    /**
     * Find most viewed images
     */
    List<Gallery> findTop10ByIsPublishedTrueOrderByViewCountDesc();

    /**
     * Find most liked images
     */
    List<Gallery> findTop10ByIsPublishedTrueOrderByLikeCountDesc();

    /**
     * Count total published images
     */
    long countByIsPublishedTrue();

    /**
     * Get total views
     */
    @Query("SELECT SUM(g.viewCount) FROM Gallery g WHERE g.isPublished = true")
    Long getTotalViews();

    // ========== RECENT UPLOADS ==========

    /**
     * Find recent uploads
     */
    List<Gallery> findTop10ByIsPublishedTrueOrderByCreatedAtDesc();
}