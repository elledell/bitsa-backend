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
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(nullable = false, unique = true, length = 100)
    private String email;

    @Column(nullable = false)
    @JsonIgnore // <-- 2. SAFETY: Never send the password to the frontend
    private String password;

    @Column(name = "student_id", unique = true, length = 50)
    private String studentId;

    @Column(length = 100)
    private String course;

    @Column
    private Integer year;

    // ========== RELATIONSHIPS ==========

    /**
     * Many Users → One Role
     * Each user has ONE role (STUDENT or ADMIN)
     */
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    /**
     * One User → Many Posts (as author)
     * A user can create multiple blog posts
     */
    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @JsonIgnore // <-- 3. FIX: Stop the infinite loop
    private Set<Post> posts = new HashSet<>();

    /**
     * One User → Many Comments
     * A user can write multiple comments
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @JsonIgnore // <-- 4. FIX: Stop the infinite loop (This fixes your specific error)
    private Set<Comment> comments = new HashSet<>();

    /**
     * One User → Many Event Registrations
     * A user can register for multiple events
     */
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @JsonIgnore // <-- 5. FIX: Stop the infinite loop
    private Set<EventRegistration> eventRegistrations = new HashSet<>();

    /**
     * One User → Many Events (as creator/organizer)
     * Admin users can create multiple events
     */
    @OneToMany(mappedBy = "creator", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @JsonIgnore // <-- 6. FIX: Stop the infinite loop
    private Set<Event> createdEvents = new HashSet<>();

    /**
     * One User → Many Gallery Images (as uploader)
     * Admin users can upload multiple gallery images
     */
    @OneToMany(mappedBy = "uploadedBy", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @JsonIgnore // <-- 7. FIX: Stop the infinite loop
    private Set<Gallery> galleryImages = new HashSet<>();

    // ========== AUDIT FIELDS ==========

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "last_login")
    private LocalDateTime lastLogin;

    // ========== ACCOUNT STATUS ==========

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_email_verified")
    @Builder.Default
    private Boolean isEmailVerified = false;

    // ========== HELPER METHODS ==========

    public boolean isAdmin() {
        return role != null && "ADMIN".equals(role.getName());
    }

    public boolean isStudent() {
        return role != null && "STUDENT".equals(role.getName());
    }

    public void addPost(Post post) {
        posts.add(post);
        post.setAuthor(this);
    }

    public void removePost(Post post) {
        posts.remove(post);
        post.setAuthor(null);
    }

    public void addComment(Comment comment) {
        comments.add(comment);
        comment.setUser(this);
    }

    public void registerForEvent(EventRegistration registration) {
        eventRegistrations.add(registration);
        registration.setUser(this);
    }

    public void createEvent(Event event) {
        createdEvents.add(event);
        event.setCreator(this);
    }

    public void uploadGalleryImage(Gallery gallery) {
        galleryImages.add(gallery);
        gallery.setUploadedBy(this);
    }
}