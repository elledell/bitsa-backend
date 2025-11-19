package com.website.bitsa.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // <-- 1. THIS IMPORT IS CRITICAL
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "categories")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(length = 100)
    private String slug;

    // ========== RELATIONSHIPS ==========

    /**
     * One Category → Many Posts
     * A category can have multiple blog posts
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @JsonIgnore // <-- 2. THIS ANNOTATION FIXES THE 500 ERROR
    private Set<Post> posts = new HashSet<>();

    // ========== DISPLAY SETTINGS ==========

    @Column(name = "icon_class", length = 50)
    private String iconClass;

    @Column(name = "color_hex", length = 7)
    private String colorHex;

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;

    // ========== STATUS FIELDS ==========

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    // ========== STATISTICS ==========

    @Column(name = "post_count")
    @Builder.Default
    private Integer postCount = 0;

    // ========== AUDIT FIELDS ==========

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== HELPER METHODS ==========

    public void addPost(Post post) {
        posts.add(post);
        post.setCategory(this);
        incrementPostCount();
    }

    public void removePost(Post post) {
        posts.remove(post);
        post.setCategory(null);
        decrementPostCount();
    }

    public void incrementPostCount() {
        this.postCount = (this.postCount == null ? 0 : this.postCount) + 1;
    }

    public void decrementPostCount() {
        if (this.postCount != null && this.postCount > 0) {
            this.postCount--;
        }
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

    public boolean hasPosts() {
        return postCount != null && postCount > 0;
    }

    public String getDisplayNameWithCount() {
        return String.format("%s (%d)", this.name, this.postCount != null ? this.postCount : 0);
    }

    // ========== STATIC CATEGORY CREATORS ==========

    public static Category createTechnologyCategory() {
        return Category.builder()
                .name("Technology")
                .description("Latest tech trends, programming tips, and software updates")
                .iconClass("fas fa-laptop-code")
                .colorHex("#3B82F6")
                .displayOrder(1)
                .isFeatured(true)
                .build();
    }

    public static Category createEventsCategory() {
        return Category.builder()
                .name("Events")
                .description("BITSA events, meetups, and announcements")
                .iconClass("fas fa-calendar-alt")
                .colorHex("#10B981")
                .displayOrder(2)
                .isFeatured(true)
                .build();
    }

    public static Category createTutorialsCategory() {
        return Category.builder()
                .name("Tutorials")
                .description("Step-by-step guides and learning resources")
                .iconClass("fas fa-graduation-cap")
                .colorHex("#F59E0B")
                .displayOrder(3)
                .isFeatured(true)
                .build();
    }

    public static Category createNewsCategory() {
        return Category.builder()
                .name("News")
                .description("BITSA news and general announcements")
                .iconClass("fas fa-newspaper")
                .colorHex("#EF4444")
                .displayOrder(4)
                .isFeatured(false)
                .build();
    }

    public static Category createStudentSpotlightCategory() {
        return Category.builder()
                .name("Student Spotlight")
                .description("Featuring outstanding BITSA members")
                .iconClass("fas fa-star")
                .colorHex("#8B5CF6")
                .displayOrder(5)
                .isFeatured(false)
                .build();
    }
}