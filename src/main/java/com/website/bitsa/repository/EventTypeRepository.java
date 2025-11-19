package com.website.bitsa.repository;


import com.website.bitsa.model.EventType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventTypeRepository extends JpaRepository<EventType, Long> {

    /**
     * Find event type by name
     */
    Optional<EventType> findByName(String name);

    /**
     * Find event type by slug
     */
    Optional<EventType> findBySlug(String slug);

    /**
     * Check if event type name exists
     */
    boolean existsByName(String name);

    /**
     * Find all active event types
     */
    List<EventType> findByIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * Find featured event types
     */
    List<EventType> findByIsFeaturedTrueAndIsActiveTrueOrderByDisplayOrderAsc();

    /**
     * Find event types with events
     */
    List<EventType> findByEventCountGreaterThanAndIsActiveTrueOrderByEventCountDesc(Integer count);

    /**
     * Find all event types ordered by event count
     */
    List<EventType> findByIsActiveTrueOrderByEventCountDesc();
}