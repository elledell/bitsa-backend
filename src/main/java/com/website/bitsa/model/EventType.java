package com.website.bitsa.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // <-- 1. IMPORT THIS
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "event_types")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;
    // "Workshop", "Meeting", "Competition", "Social Event", "Hackathon"

    @Column(length = 500)
    private String description;
    // "Technical workshops and hands-on sessions"

    @Column(length = 100)
    private String slug;
    // URL-friendly: "workshop", "meeting", "competition"

    // ========== RELATIONSHIPS ==========

    /**
     * One EventType → Many Events
     * An event type can have multiple events
     */
    @OneToMany(mappedBy = "eventType", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude  // Prevent infinite recursion
    @Builder.Default
    @JsonIgnore // <-- 2. THIS IS THE CRITICAL FIX (Stops infinite loop)
    private Set<Event> events = new HashSet<>();

    // ========== DISPLAY SETTINGS ==========

    @Column(name = "icon_class", length = 50)
    private String iconClass;
    // CSS icon class: "fas fa-laptop", "fas fa-users", "fas fa-trophy"

    @Column(name = "color_hex", length = 7)
    private String colorHex;
    // "#3B82F6", "#10B981", "#F59E0B" for UI theming

    @Column(name = "badge_color", length = 50)
    private String badgeColor;
    // Tailwind class: "bg-blue-500", "bg-green-500"

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;
    // For sorting event types in UI

    // ========== EVENT TYPE SETTINGS ==========

    @Column(name = "requires_registration")
    @Builder.Default
    private Boolean requiresRegistration = true;
    // Some events don't need registration

    @Column(name = "has_capacity_limit")
    @Builder.Default
    private Boolean hasCapacityLimit = false;
    // Does this type typically have seat limits?

    @Column(name = "default_duration_minutes")
    private Integer defaultDurationMinutes;
    // Default duration: 60, 120, 180 minutes

    // ========== STATUS FIELDS ==========

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;
    // Can disable event types temporarily

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;
    // Show in featured event types section

    // ========== STATISTICS ==========

    @Column(name = "event_count")
    @Builder.Default
    private Integer eventCount = 0;
    // Cache of total events of this type

    @Column(name = "total_attendees")
    @Builder.Default
    private Integer totalAttendees = 0;
    // Total people who attended events of this type

    // ========== AUDIT FIELDS ==========

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== HELPER METHODS ==========

    public void addEvent(Event event) {
        events.add(event);
        event.setEventType(this);
        incrementEventCount();
    }

    public void removeEvent(Event event) {
        events.remove(event);
        event.setEventType(null);
        decrementEventCount();
    }

    public void incrementEventCount() {
        this.eventCount = (this.eventCount == null ? 0 : this.eventCount) + 1;
    }

    public void decrementEventCount() {
        if (this.eventCount != null && this.eventCount > 0) {
            this.eventCount--;
        }
    }

    public void addAttendees(int count) {
        this.totalAttendees = (this.totalAttendees == null ? 0 : this.totalAttendees) + count;
    }

    @PrePersist
    @PreUpdate
    public void generateSlug() {
        if (this.slug == null || this.slug.isEmpty()) {
            this.slug = this.name
                    .toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "")
                    .replaceAll("\\s+", "-")
                    .replaceAll("-+", "-")
                    .trim();
        }
    }

    public boolean hasEvents() {
        return eventCount != null && eventCount > 0;
    }

    public String getDisplayNameWithCount() {
        return String.format("%s (%d events)", this.name, this.eventCount != null ? this.eventCount : 0);
    }

    public double getAverageAttendance() {
        if (eventCount == null || eventCount == 0 || totalAttendees == null) {
            return 0.0;
        }
        return (double) totalAttendees / eventCount;
    }

    // ========== STATIC EVENT TYPE CREATORS ==========

    public static EventType createWorkshopType() {
        return EventType.builder()
                .name("Workshop")
                .description("Technical workshops and hands-on training sessions")
                .iconClass("fas fa-laptop-code")
                .colorHex("#3B82F6")
                .badgeColor("bg-blue-500")
                .displayOrder(1)
                .requiresRegistration(true)
                .hasCapacityLimit(true)
                .defaultDurationMinutes(120)
                .isFeatured(true)
                .build();
    }

    public static EventType createMeetingType() {
        return EventType.builder()
                .name("Meeting")
                .description("General BITSA meetings and discussions")
                .iconClass("fas fa-users")
                .colorHex("#10B981")
                .badgeColor("bg-green-500")
                .displayOrder(2)
                .requiresRegistration(false)
                .hasCapacityLimit(false)
                .defaultDurationMinutes(60)
                .isFeatured(true)
                .build();
    }

    public static EventType createCompetitionType() {
        return EventType.builder()
                .name("Competition")
                .description("Coding competitions, hackathons, and challenges")
                .iconClass("fas fa-trophy")
                .colorHex("#F59E0B")
                .badgeColor("bg-yellow-500")
                .displayOrder(3)
                .requiresRegistration(true)
                .hasCapacityLimit(true)
                .defaultDurationMinutes(240)
                .isFeatured(true)
                .build();
    }

    public static EventType createSocialType() {
        return EventType.builder()
                .name("Social Event")
                .description("Networking events, meetups, and social gatherings")
                .iconClass("fas fa-glass-cheers")
                .colorHex("#EC4899")
                .badgeColor("bg-pink-500")
                .displayOrder(4)
                .requiresRegistration(false)
                .hasCapacityLimit(false)
                .defaultDurationMinutes(90)
                .isFeatured(false)
                .build();
    }

    public static EventType createHackathonType() {
        return EventType.builder()
                .name("Hackathon")
                .description("Extended coding marathons and project development")
                .iconClass("fas fa-code")
                .colorHex("#8B5CF6")
                .badgeColor("bg-purple-500")
                .displayOrder(5)
                .requiresRegistration(true)
                .hasCapacityLimit(true)
                .defaultDurationMinutes(480)
                .isFeatured(true)
                .build();
    }

    public static EventType createSeminarType() {
        return EventType.builder()
                .name("Seminar")
                .description("Educational talks and presentations")
                .iconClass("fas fa-chalkboard-teacher")
                .colorHex("#EF4444")
                .badgeColor("bg-red-500")
                .displayOrder(6)
                .requiresRegistration(true)
                .hasCapacityLimit(true)
                .defaultDurationMinutes(90)
                .isFeatured(false)
                .build();
    }

    public static EventType createWebinarType() {
        return EventType.builder()
                .name("Webinar")
                .description("Online workshops and virtual events")
                .iconClass("fas fa-video")
                .colorHex("#06B6D4")
                .badgeColor("bg-cyan-500")
                .displayOrder(7)
                .requiresRegistration(true)
                .hasCapacityLimit(false)
                .defaultDurationMinutes(60)
                .isFeatured(false)
                .build();
    }
}