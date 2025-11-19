package com.website.bitsa.model;

import com.fasterxml.jackson.annotation.JsonIgnore; // <-- 1. IMPORT THIS
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "comments")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Comment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String commentText;

    // ========== RELATIONSHIPS ==========

    /**
     * Many Comments → One Post
     * Each comment belongs to one post
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "post_id", nullable = false)
    @JsonIgnore // <-- 2. CRITICAL FIX: Stops infinite loop (Comment -> Post -> Comments...)
    private Post post;

    /**
     * Many Comments → One User
     * Each comment is written by one user
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    // We keep the User visible so we know who wrote the comment.
    // (Make sure User.java has @JsonIgnore on its 'comments' list!)
    private User user;

    // ========== MODERATION FIELDS ==========

    @Column(name = "is_approved")
    @Builder.Default
    private Boolean isApproved = false; // Admin must approve comments

    @Column(name = "is_flagged")
    @Builder.Default
    private Boolean isFlagged = false; // Flagged for review

    @Column(name = "is_edited")
    @Builder.Default
    private Boolean isEdited = false; // Track if comment was edited

    // ========== ENGAGEMENT ==========

    @Column(name = "like_count")
    @Builder.Default
    private Integer likeCount = 0;

    // ========== AUDIT FIELDS ==========

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "approved_by")
    private Long approvedBy;

    // ========== HELPER METHODS ==========

    public void approve(Long adminUserId) {
        this.isApproved = true;
        this.approvedAt = LocalDateTime.now();
        this.approvedBy = adminUserId;
    }

    public void flag() {
        this.isFlagged = true;
    }

    public void unflag() {
        this.isFlagged = false;
    }

    public void markAsEdited() {
        this.isEdited = true;
    }

    public void incrementLikeCount() {
        this.likeCount = (this.likeCount == null ? 0 : this.likeCount) + 1;
    }

    public void decrementLikeCount() {
        if (this.likeCount != null && this.likeCount > 0) {
            this.likeCount--;
        }
    }

    public boolean isPending() {
        return !this.isApproved && !this.isFlagged;
    }

    public String getPreview() {
        if (this.commentText.length() <= 100) {
            return this.commentText;
        }
        return this.commentText.substring(0, 97) + "...";
    }
}