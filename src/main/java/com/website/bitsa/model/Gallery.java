package com.website.bitsa.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "gallery")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Gallery {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 200)
    private String title;  // "BITSA Hackathon 2024"

    @Column(length = 500)
    private String description;  // "Photos from our annual hackathon"

    @Column(nullable = false, length = 500)
    private String imageUrl;  // "/uploads/gallery/hackathon-2024.jpg"

    @Column(length = 200)
    private String altText;  // "Students coding at hackathon"

    // ========== RELATIONSHIPS ==========

    /**
     * Many Gallery Images → One User (uploader)
     * Each image is uploaded by one admin
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "uploaded_by", nullable = false)
    @JsonIgnore
    private User uploadedBy;

    // ========== IMAGE METADATA ==========

    @Column(name = "file_size")
    private Long fileSize;  // In bytes

    @Column(name = "file_type", length = 50)
    private String fileType;  // "image/jpeg", "image/png"

    @Column(name = "width")
    private Integer width;  // Image width in pixels

    @Column(name = "height")
    private Integer height;  // Image height in pixels

    // ========== CATEGORIZATION ==========

    @Column(length = 100)
    private String category;  // "Events", "Workshops", "Team", "Campus"

    @Column(length = 500)
    private String tags;  // Comma-separated: "hackathon,2024,coding"

    @Column(name = "event_date")
    private LocalDateTime eventDate;  // When photo was taken

    // ========== DISPLAY SETTINGS ==========

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;  // Show in featured gallery

    @Column(name = "display_order")
    @Builder.Default
    private Integer displayOrder = 0;  // Sort order

    @Column(name = "is_published")
    @Builder.Default
    private Boolean isPublished = true;  // Visible to public

    // ========== ENGAGEMENT ==========

    @Column(name = "view_count")
    @Builder.Default
    private Integer viewCount = 0;

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

    // ========== HELPER METHODS ==========

    /**
     * Increment view count
     */
    public void incrementViewCount() {
        this.viewCount = (this.viewCount == null ? 0 : this.viewCount) + 1;
    }

    /**
     * Increment like count
     */
    public void incrementLikeCount() {
        this.likeCount = (this.likeCount == null ? 0 : this.likeCount) + 1;
    }

    /**
     * Decrement like count
     */
    public void decrementLikeCount() {
        if (this.likeCount != null && this.likeCount > 0) {
            this.likeCount--;
        }
    }

    /**
     * Get tags as array
     */
    public String[] getTagsArray() {
        if (this.tags == null || this.tags.isEmpty()) {
            return new String[0];
        }
        return this.tags.split(",");
    }

    /**
     * Set tags from array
     */
    public void setTagsFromArray(String[] tagsArray) {
        this.tags = String.join(",", tagsArray);
    }

    /**
     * Get file size in KB
     */
    public Double getFileSizeInKB() {
        if (this.fileSize == null) {
            return 0.0;
        }
        return this.fileSize / 1024.0;
    }

    /**
     * Get file size in MB
     */
    public Double getFileSizeInMB() {
        if (this.fileSize == null) {
            return 0.0;
        }
        return this.fileSize / (1024.0 * 1024.0);
    }

    /**
     * Check if image is landscape
     */
    public boolean isLandscape() {
        if (width == null || height == null) {
            return false;
        }
        return width > height;
    }

    /**
     * Check if image is portrait
     */
    public boolean isPortrait() {
        if (width == null || height == null) {
            return false;
        }
        return height > width;
    }

    /**
     * Get aspect ratio
     */
    public String getAspectRatio() {
        if (width == null || height == null) {
            return "Unknown";
        }
        int gcd = gcd(width, height);
        return (width / gcd) + ":" + (height / gcd);
    }

    /**
     * Helper: Calculate GCD for aspect ratio
     */
    private int gcd(int a, int b) {
        return b == 0 ? a : gcd(b, a % b);
    }
}
