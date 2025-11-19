package com.website.bitsa.model;


import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "contact_messages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContactMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;  // Sender's name

    @Column(nullable = false, length = 100)
    private String email;  // Sender's email

    @Column(length = 20)
    private String phone;  // Optional phone number

    @Column(length = 200)
    private String subject;  // Message subject

    @Column(nullable = false, columnDefinition = "TEXT")
    private String message;  // Message content

    // ========== CATEGORIZATION ==========

    @Column(length = 50)
    @Builder.Default
    private String category = "GENERAL";  // GENERAL, MEMBERSHIP, EVENT, COMPLAINT, SUGGESTION

    @Column(length = 50)
    @Builder.Default
    private String priority = "NORMAL";  // LOW, NORMAL, HIGH, URGENT

    // ========== STATUS TRACKING ==========

    @Column(name = "is_read")
    @Builder.Default
    private Boolean isRead = false;  // Has admin read this?

    @Column(name = "read_at")
    private LocalDateTime readAt;  // When was it read?

    @Column(name = "read_by")
    private Long readBy;  // Admin user ID who read it

    @Column(name = "is_replied")
    @Builder.Default
    private Boolean isReplied = false;  // Has admin replied?

    @Column(name = "replied_at")
    private LocalDateTime repliedAt;

    @Column(name = "replied_by")
    private Long repliedBy;  // Admin user ID who replied

    @Column(name = "reply_message", columnDefinition = "TEXT")
    private String replyMessage;  // Admin's reply

    // ========== SPAM DETECTION ==========

    @Column(name = "is_spam")
    @Builder.Default
    private Boolean isSpam = false;  // Marked as spam

    @Column(name = "spam_score")
    private Double spamScore;  // 0.0 - 1.0 (future spam detection)

    // ========== METADATA ==========

    @Column(name = "ip_address", length = 50)
    private String ipAddress;  // Track sender IP

    @Column(name = "user_agent", length = 500)
    private String userAgent;  // Browser info

    @Column(name = "referrer", length = 500)
    private String referrer;  // Where they came from

    // ========== AUDIT FIELDS ==========

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== HELPER METHODS ==========

    /**
     * Mark message as read
     */
    public void markAsRead(Long adminUserId) {
        this.isRead = true;
        this.readAt = LocalDateTime.now();
        this.readBy = adminUserId;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Mark message as unread
     */
    public void markAsUnread() {
        this.isRead = false;
        this.readAt = null;
        this.readBy = null;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Reply to message
     */
    public void reply(String replyText, Long adminUserId) {
        this.isReplied = true;
        this.repliedAt = LocalDateTime.now();
        this.repliedBy = adminUserId;
        this.replyMessage = replyText;
        this.updatedAt = LocalDateTime.now();

        // Auto-mark as read when replying
        if (!this.isRead) {
            markAsRead(adminUserId);
        }
    }

    /**
     * Mark as spam
     */
    public void markAsSpam() {
        this.isSpam = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Mark as not spam
     */
    public void markAsNotSpam() {
        this.isSpam = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Set priority
     */
    public void setPriorityLevel(String priority) {
        this.priority = priority.toUpperCase();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Check if message is pending (unread and not replied)
     */
    public boolean isPending() {
        return !isRead && !isReplied && !isSpam;
    }

    /**
     * Check if message needs attention (read but not replied)
     */
    public boolean needsAttention() {
        return isRead && !isReplied && !isSpam;
    }

    /**
     * Get message preview (first 100 chars)
     */
    public String getMessagePreview() {
        if (this.message.length() <= 100) {
            return this.message;
        }
        return this.message.substring(0, 97) + "...";
    }

    /**
     * Check if message is recent (within last 24 hours)
     */
    public boolean isRecent() {
        return this.createdAt.isAfter(LocalDateTime.now().minusHours(24));
    }

    /**
     * Update timestamp
     */
    @PreUpdate
    public void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}