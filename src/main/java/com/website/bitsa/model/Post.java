package com.website.bitsa.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // <-- 1. MAKE SURE THIS IS IMPORTED
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "posts")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Post {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(length = 500)
    private String excerpt;

    @Column(length = 200)
    private String slug;

    // ========== RELATIONSHIPS ==========

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id", nullable = false)
    private User author;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @OneToMany(mappedBy = "post", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @JsonIgnore // <-- 2. THIS IS THE FIX. It stops the loop.
    private Set<Comment> comments = new HashSet<>();

    // ========== MEDIA FIELDS ==========

    @Column(name = "featured_image", length = 500)
    private String featuredImage;

    @Column(name = "featured_image_alt", length = 200)
    private String featuredImageAlt;

    // ========== PUBLISHING SETTINGS ==========

    @Column(name = "is_published")
    @Builder.Default
    private Boolean isPublished = false;

    @Column(name = "published_at")
    private LocalDateTime publishedAt;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "is_pinned")
    @Builder.Default
    private Boolean isPinned = false;

    // ========== ENGAGEMENT METRICS ==========

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

    @Column(name = "like_count")
    @Builder.Default
    private Integer likeCount = 0;

    @Column(name = "comment_count")
    @Builder.Default
    private Integer commentCount = 0;

    @Column(name = "share_count")
    @Builder.Default
    private Integer shareCount = 0;

    // ========== SEO FIELDS ==========

    @Column(name = "meta_title", length = 200)
    private String metaTitle;

    @Column(name = "meta_description", length = 500)
    private String metaDescription;

    @Column(name = "meta_keywords", length = 500)
    private String metaKeywords;

    // ========== CONTENT METADATA ==========

    @Column(name = "reading_time_minutes")
    private Integer readingTimeMinutes;

    @Column(name = "tags", length = 500)
    private String tags;

    @Column(name = "language", length = 10)
    @Builder.Default
    private String language = "en";

    // ========== MODERATION ==========

    @Column(name = "comments_enabled")
    @Builder.Default
    private Boolean commentsEnabled = true;

    @Column(name = "is_locked")
    @Builder.Default
    private Boolean isLocked = false;

    // ========== AUDIT FIELDS ==========

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_viewed_at")
    private LocalDateTime lastViewedAt;

    // ========== HELPER METHODS ==========

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setPost(this);
        incrementCommentCount();
    }

    public void removeComment(Comment comment) {
        comments.remove(comment);
        comment.setPost(null);
        decrementCommentCount();
    }

    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
        this.lastViewedAt = LocalDateTime.now();
    }

    public void incrementLikeCount() {
        this.likeCount = (this.likeCount == null ? 0 : this.likeCount) + 1;
    }

    public void decrementLikeCount() {
        if (this.likeCount != null && this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public void incrementCommentCount() {
        this.commentCount = (this.commentCount == null ? 0 : this.commentCount) + 1;
    }

    public void decrementCommentCount() {
        if (this.commentCount != null && this.commentCount > 0) {
            this.commentCount--;
        }
    }

    public void incrementShareCount() {
        this.shareCount = (this.shareCount == null ? 0 : this.shareCount) + 1;
    }

    @PrePersist
    @PreUpdate
    public void generateSlugAndMetadata() {
        if (this.slug == null || this.slug.isEmpty()) {
            this.slug = this.title
                    .toLowerCase()
                    .replaceAll("[^a-z0-9\\s-]", "")
                    .replaceAll("\\s+", "-")
                    .replaceAll("-+", "-")
                    .trim();
        }

        if (this.readingTimeMinutes == null && this.content != null) {
            int wordCount = this.content.split("\\s+").length;
            this.readingTimeMinutes = Math.max(1, wordCount / 200);
        }

        if (this.isPublished && this.publishedAt == null) {
            this.publishedAt = LocalDateTime.now();
        }
    }

    public void publish() {
        this.isPublished = true;
        this.publishedAt = LocalDateTime.now();
    }

    public void unpublish() {
        this.isPublished = false;
        this.publishedAt = null;
    }

    public boolean isDraft() {
        return !this.isPublished;
    }

    public int getTotalEngagement() {
        int views = this.viewCount != null ? this.viewCount : 0;
        int likes = this.likeCount != null ? this.likeCount : 0;
        int comments = this.commentCount != null ? this.commentCount : 0;
        int shares = this.shareCount != null ? this.shareCount : 0;
        return views + (likes * 2) + (comments * 3) + (shares * 5);
    }

    public String[] getTagsArray() {
        if (this.tags == null || this.tags.isEmpty()) {
            return new String[0];
        }
        return this.tags.split(",");
    }

    public void setTagsFromArray(String[] tagsArray) {
        this.tags = String.join(",", tagsArray);
    }

    public boolean isRecent() {
        if (this.publishedAt == null) {
            return false;
        }
        return this.publishedAt.isAfter(LocalDateTime.now().minusDays(7));
    }

    public String getShortTitle() {
        if (this.title.length() <= 50) {
            return this.title;
        }
        return this.title.substring(0, 47) + "...";
    }
}