package com.website.bitsa.repository;


import com.website.bitsa.model.EventRegistration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventRegistrationRepository extends JpaRepository<EventRegistration, Long> {

    // ========== EVENT QUERIES ==========

    /**
     * Find all registrations for an event
     */
    List<EventRegistration> findByEventIdOrderByRegistrationDateAsc(Long eventId);

    /**
     * Find active registrations for an event (not cancelled)
     */
    List<EventRegistration> findByEventIdAndIsCancelledFalseOrderByRegistrationDateAsc(Long eventId);

    /**
     * Find confirmed registrations (not waitlisted, not cancelled)
     */
    List<EventRegistration> findByEventIdAndIsWaitlistedFalseAndIsCancelledFalseOrderByRegistrationDateAsc(Long eventId);

    /**
     * Find waitlisted registrations
     */
    List<EventRegistration> findByEventIdAndIsWaitlistedTrueOrderByWaitlistPositionAsc(Long eventId);

    /**
     * Count registrations for an event
     */
    long countByEventIdAndIsCancelledFalse(Long eventId);

    /**
     * Count confirmed registrations (not waitlisted)
     */
    long countByEventIdAndIsWaitlistedFalseAndIsCancelledFalse(Long eventId);

    // ========== USER QUERIES ==========

    /**
     * Find all registrations by user
     */
    List<EventRegistration> findByUserIdOrderByRegistrationDateDesc(Long userId);

    /**
     * Find active registrations by user (not cancelled)
     */
    List<EventRegistration> findByUserIdAndIsCancelledFalseOrderByRegistrationDateDesc(Long userId);

    /**
     * Find upcoming event registrations for user
     */
    @Query("SELECT r FROM EventRegistration r WHERE r.user.id = :userId AND r.isCancelled = false AND " +
            "r.event.dateTime > CURRENT_TIMESTAMP ORDER BY r.event.dateTime ASC")
    List<EventRegistration> findUpcomingRegistrationsByUser(@Param("userId") Long userId);

    /**
     * Count registrations by user
     */
    long countByUserIdAndIsCancelledFalse(Long userId);

    // ========== DUPLICATE CHECK ==========

    /**
     * Check if user already registered for event
     */
    boolean existsByEventIdAndUserId(Long eventId, Long userId);

    /**
     * Find registration by event and user
     */
    Optional<EventRegistration> findByEventIdAndUserId(Long eventId, Long userId);

    /**
     * Check if user has active registration (not cancelled)
     */
    boolean existsByEventIdAndUserIdAndIsCancelledFalse(Long eventId, Long userId);

    // ========== ATTENDANCE TRACKING ==========

    /**
     * Find registrations by attendance status
     */
    List<EventRegistration> findByEventIdAndAttendanceStatus(Long eventId, String status);

    /**
     * Find attendees who checked in
     */
    List<EventRegistration> findByEventIdAndCheckedInTrue(Long eventId);

    /**
     * Count checked-in attendees
     */
    long countByEventIdAndCheckedInTrue(Long eventId);

    // ========== STATISTICS ==========

    /**
     * Count total active registrations
     */
    long countByIsCancelledFalse();

    /**
     * Find recent registrations
     */
    List<EventRegistration> findTop10ByOrderByRegistrationDateDesc();

    /**
     * Count registrations by status
     */
    @Query("SELECT COUNT(r) FROM EventRegistration r WHERE r.attendanceStatus = :status")
    long countByAttendanceStatus(@Param("status") String status);
}