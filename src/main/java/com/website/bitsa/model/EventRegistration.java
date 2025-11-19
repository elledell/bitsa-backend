package com.website.bitsa.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "event_registrations",
        uniqueConstraints = @UniqueConstraint(columnNames = {"event_id", "user_id"}))
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // ========== RELATIONSHIPS ==========

    /**
     * Many Registrations → One Event
     * Each registration is for one event
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_id", nullable = false)
    private Event event;

    /**
     * Many Registrations → One User
     * Each registration belongs to one user
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // ========== REGISTRATION DETAILS ==========

    @Column(name = "registration_date", nullable = false)
    @CreationTimestamp
    private LocalDateTime registrationDate;

    @Column(name = "attendance_status", length = 50)
    @Builder.Default
    private String attendanceStatus = "REGISTERED";  // REGISTERED, ATTENDED, NO_SHOW, CANCELLED

    @Column(name = "is_waitlisted")
    @Builder.Default
    private Boolean isWaitlisted = false;  // On waitlist or confirmed

    @Column(name = "waitlist_position")
    private Integer waitlistPosition;  // Position in waitlist

    // ========== ADDITIONAL INFO ==========

    @Column(name = "special_requirements", length = 500)
    private String specialRequirements;  // Dietary restrictions, accessibility needs

    @Column(name = "notes", length = 1000)
    private String notes;  // Additional notes from user

    // ========== NOTIFICATION TRACKING ==========

    @Column(name = "reminder_sent")
    @Builder.Default
    private Boolean reminderSent = false;  // Email reminder sent

    @Column(name = "confirmation_sent")
    @Builder.Default
    private Boolean confirmationSent = false;  // Registration confirmation sent

    // ========== CHECK-IN TRACKING ==========

    @Column(name = "checked_in")
    @Builder.Default
    private Boolean checkedIn = false;  // Did they show up?

    @Column(name = "check_in_time")
    private LocalDateTime checkInTime;

    @Column(name = "checked_in_by")
    private Long checkedInBy;  // Admin who checked them in

    // ========== CANCELLATION ==========

    @Column(name = "is_cancelled")
    @Builder.Default
    private Boolean isCancelled = false;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    // ========== AUDIT FIELDS ==========

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== HELPER METHODS ==========

    /**
     * Mark as attended
     */
    public void markAsAttended(Long adminUserId) {
        this.attendanceStatus = "ATTENDED";
        this.checkedIn = true;
        this.checkInTime = LocalDateTime.now();
        this.checkedInBy = adminUserId;
    }

    /**
     * Mark as no-show
     */
    public void markAsNoShow() {
        this.attendanceStatus = "NO_SHOW";
        this.checkedIn = false;
    }

    /**
     * Cancel registration
     */
    public void cancel(String reason) {
        this.isCancelled = true;
        this.cancelledAt = LocalDateTime.now();
        this.cancellationReason = reason;
        this.attendanceStatus = "CANCELLED";
    }

    /**
     * Move from waitlist to confirmed
     */
    public void confirmFromWaitlist() {
        this.isWaitlisted = false;
        this.waitlistPosition = null;
        this.attendanceStatus = "REGISTERED";
    }

    /**
     * Add to waitlist
     */
    public void addToWaitlist(int position) {
        this.isWaitlisted = true;
        this.waitlistPosition = position;
    }

    /**
     * Send confirmation email flag
     */
    public void markConfirmationSent() {
        this.confirmationSent = true;
    }

    /**
     * Send reminder email flag
     */
    public void markReminderSent() {
        this.reminderSent = true;
    }

    /**
     * Check if registration is active
     */
    public boolean isActive() {
        return !isCancelled && "REGISTERED".equals(attendanceStatus);
    }

    /**
     * Update timestamp
     */
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}