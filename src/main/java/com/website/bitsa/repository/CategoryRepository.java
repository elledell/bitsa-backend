package com.website.bitsa.repository;



import com.website.bitsa.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Find category by name
     */
    Optional<Category> findByName(String name);

    /**
     * Find category by slug
     */
    Optional<Category> findBySlug(String slug);

    /**
     * Check if category name exists
     */
    boolean existsByName(String name);

    /**
     * Find all active categories
     */
    List<Category> findByIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * Find featured categories
     */
    List<Category> findByIsFeaturedTrueAndIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * Find categories with posts
     */
    List<Category> findByPostCountGreaterThanAndIsActiveTrueOrderByPostCountDesc(Integer count);

    /**
     * Find all categories ordered by post count
     */
    List<Category> findByIsActiveTrueOrderByPostCountDesc();
}