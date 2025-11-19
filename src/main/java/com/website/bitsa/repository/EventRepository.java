package com.website.bitsa.repository;

import com.website.bitsa.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {

    // --- 1. Find UPCOMING events (Public) ---
    @Query("SELECT e FROM Event e WHERE e.dateTime > :now AND e.isPublished = true AND e.isCancelled = false ORDER BY e.dateTime ASC")
    List<Event> findUpcomingEvents(@Param("now") LocalDateTime now);

    // --- 2. Find PAST events (Public) ---
    @Query("SELECT e FROM Event e WHERE e.dateTime < :now AND e.isPublished = true ORDER BY e.dateTime DESC")
    List<Event> findPastEvents(@Param("now") LocalDateTime now);

    // --- 3. Find FEATURED upcoming events (Public) ---
    @Query("SELECT e FROM Event e WHERE e.isFeatured = true AND e.dateTime > :now AND e.isPublished = true ORDER BY e.dateTime ASC")
    List<Event> findFeaturedUpcomingEvents(@Param("now") LocalDateTime now);

    // --- 4. Find events by TYPE (Public) ---
    @Query("SELECT e FROM Event e WHERE e.eventType.id = :typeId AND e.dateTime > :now AND e.isPublished = true ORDER BY e.dateTime ASC")
    List<Event> findUpcomingEventsByType(@Param("typeId") Long typeId, @Param("now") LocalDateTime now);

    // --- 5. Find single event by SLUG (Public) ---
    Optional<Event> findBySlugAndIsPublishedTrue(String slug);

    // --- 6. Find ALL events sorted by date (Admin) ---
    List<Event> findAllByOrderByDateTimeDesc();

    // --- 7. Count Upcoming Events (Dashboard) ---
    // THIS WAS THE MISSING METHOD CAUSING THE ERROR
    @Query("SELECT COUNT(e) FROM Event e WHERE e.dateTime > :now AND e.isPublished = true AND e.isCancelled = false")
    long countUpcomingEvents(@Param("now") LocalDateTime now);
}