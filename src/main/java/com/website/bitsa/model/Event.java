package com.website.bitsa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "events")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;  // "React Workshop 2024"

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;  // Full event description

    @Column(nullable = false)
    private LocalDateTime dateTime;  // Event date and time

    @Column(nullable = false, length = 200)
    private String location;  // "Lab 3, Main Campus" or "Zoom Link"

    @Column(length = 200)
    private String slug;  // URL-friendly: "react-workshop-2024"

    // ========== RELATIONSHIPS ==========

    /**
     * Many Events → One EventType
     * Each event has one type (Workshop, Meeting, etc.)
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "event_type_id", nullable = false)
    private EventType eventType;

    /**
     * Many Events → One User (creator)
     * Each event is created by one admin
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creator_id", nullable = false)
    private User creator;

    /**
     * One Event → Many EventRegistrations
     * An event can have multiple registrations
     */
    @OneToMany(mappedBy = "event", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @JsonIgnore // <-- ADD THIS
    private Set<EventRegistration> registrations = new HashSet<>();

    // ========== CAPACITY MANAGEMENT ==========

    @Column(name = "max_attendees")
    private Integer maxAttendees;  // Maximum capacity (null = unlimited)

    @Column(name = "current_attendees")
    @Builder.Default
    private Integer currentAttendees = 0;  // Current registration count

    @Column(name = "waitlist_enabled")
    @Builder.Default
    private Boolean waitlistEnabled = false;  // Enable waitlist when full

    // ========== MEDIA FIELDS ==========

    @Column(name = "featured_image", length = 500)
    private String featuredImage;  // Event banner/poster

    @Column(name = "featured_image_alt", length = 200)
    private String featuredImageAlt;

    // ========== EVENT DETAILS ==========

    @Column(name = "duration_minutes")
    private Integer durationMinutes;  // 60, 120, 180 minutes

    @Column(name = "meeting_link", length = 500)
    private String meetingLink;  // Zoom/Google Meet link for virtual events

    @Column(name = "requirements", length = 1000)
    private String requirements;  // "Bring laptop, install VS Code"

    @Column(name = "agenda", columnDefinition = "TEXT")
    private String agenda;  // Event schedule/topics

    // ========== REGISTRATION SETTINGS ==========

    @Column(name = "registration_required")
    @Builder.Default
    private Boolean registrationRequired = true;

    @Column(name = "registration_opens_at")
    private LocalDateTime registrationOpensAt;  // When registration starts

    @Column(name = "registration_closes_at")
    private LocalDateTime registrationClosesAt;  // Registration deadline

    // ========== PUBLISHING SETTINGS ==========

    @Column(name = "is_published")
    @Builder.Default
    private Boolean isPublished = false;  // Draft vs Published

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;  // Show on homepage

    @Column(name = "is_cancelled")
    @Builder.Default
    private Boolean isCancelled = false;  // Event cancelled

    @Column(name = "cancellation_reason", length = 500)
    private String cancellationReason;

    // ========== ENGAGEMENT METRICS ==========

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "share_count")
    @Builder.Default
    private Integer shareCount = 0;

    // ========== AUDIT FIELDS ==========

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== HELPER METHODS ==========

    /**
     * Add registration (bidirectional helper)
     */
    public void addRegistration(EventRegistration registration) {
        registrations.add(registration);
        registration.setEvent(this);
        incrementAttendeeCount();
    }

    /**
     * Remove registration
     */
    public void removeRegistration(EventRegistration registration) {
        registrations.remove(registration);
        registration.setEvent(null);
        decrementAttendeeCount();
    }

    /**
     * Increment attendee count
     */
    public void incrementAttendeeCount() {
        this.currentAttendees = (this.currentAttendees == null ? 0 : this.currentAttendees) + 1;
    }

    /**
     * Decrement attendee count
     */
    public void decrementAttendeeCount() {
        if (this.currentAttendees != null && this.currentAttendees > 0) {
            this.currentAttendees--;
        }
    }

    /**
     * Check if event is full
     */
    public boolean isFull() {
        if (maxAttendees == null) {
            return false;  // Unlimited capacity
        }
        return currentAttendees >= maxAttendees;
    }

    /**
     * Check if registration is open
     */
    public boolean isRegistrationOpen() {
        LocalDateTime now = LocalDateTime.now();

        // Check if registration period is defined
        if (registrationOpensAt != null && now.isBefore(registrationOpensAt)) {
            return false;  // Not opened yet
        }

        if (registrationClosesAt != null && now.isAfter(registrationClosesAt)) {
            return false;  // Already closed
        }

        // Check if event is cancelled or full
        if (isCancelled || (isFull() && !waitlistEnabled)) {
            return false;
        }

        return registrationRequired && isPublished;
    }

    /**
     * Check if event is upcoming
     */
    public boolean isUpcoming() {
        return dateTime.isAfter(LocalDateTime.now()) && !isCancelled;
    }

    /**
     * Check if event is past
     */
    public boolean isPast() {
        return dateTime.isBefore(LocalDateTime.now());
    }

    /**
     * Check if event is today
     */
    public boolean isToday() {
        LocalDateTime now = LocalDateTime.now();
        return dateTime.toLocalDate().equals(now.toLocalDate());
    }

    /**
     * Get available seats
     */
    public Integer getAvailableSeats() {
        if (maxAttendees == null) {
            return null;  // Unlimited
        }
        return maxAttendees - (currentAttendees != null ? currentAttendees : 0);
    }

    /**
     * Get end time
     */
    public LocalDateTime getEndTime() {
        if (durationMinutes == null) {
            return dateTime.plusHours(1);  // Default 1 hour
        }
        return dateTime.plusMinutes(durationMinutes);
    }

    /**
     * Cancel event
     */
    public void cancel(String reason) {
        this.isCancelled = true;
        this.cancellationReason = reason;
    }

    /**
     * Generate slug from title
     */
    @PrePersist
    @PreUpdate
    public void generateSlug() {
        if (this.slug == null || this.slug.isEmpty()) {
            this.slug = this.title
                    .toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "")
                    .replaceAll("\\s+", "-")
                    .replaceAll("-+", "-")
                    .trim();
        }
    }

    /**
     * Increment view count
     */
    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }
}