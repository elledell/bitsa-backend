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
@Table(name = "roles")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @Column(length = 200)
    private String description;

    // ========== RELATIONSHIPS ==========

    /**
     * One Role → Many Users
     * Each role can be assigned to multiple users
     */
    @OneToMany(mappedBy = "role", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude
    @Builder.Default
    @JsonIgnore // <-- 2. THIS FIXES THE FINAL LOOP (Role -> Users -> Role...)
    private Set<User> users = new HashSet<>();

    // ========== PERMISSION FIELDS ==========

    @Column(name = "can_manage_users")
    @Builder.Default
    private Boolean canManageUsers = false;

    @Column(name = "can_manage_posts")
    @Builder.Default
    private Boolean canManagePosts = false;

    @Column(name = "can_manage_events")
    @Builder.Default
    private Boolean canManageEvents = false;

    @Column(name = "can_manage_gallery")
    @Builder.Default
    private Boolean canManageGallery = false;

    @Column(name = "can_comment")
    @Builder.Default
    private Boolean canComment = true;

    @Column(name = "can_register_events")
    @Builder.Default
    private Boolean canRegisterEvents = true;

    // ========== AUDIT FIELDS ==========

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // ========== STATUS FIELD ==========

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    // ========== HELPER METHODS ==========

    public boolean isAdminRole() {
        return "ADMIN".equals(this.name);
    }

    public boolean isStudentRole() {
        return "STUDENT".equals(this.name);
    }

    public void addUser(User user) {
        users.add(user);
        user.setRole(this);
    }

    public void removeUser(User user) {
        users.remove(user);
        user.setRole(null);
    }

    public boolean hasAdminPrivileges() {
        return canManageUsers || canManagePosts || canManageEvents || canManageGallery;
    }

    public String getDisplayName() {
        return this.name.substring(0, 1).toUpperCase() + this.name.substring(1).toLowerCase();
    }

    // ========== STATIC ROLE CREATORS ==========

    public static Role createAdminRole() {
        return Role.builder()
                .name("ADMIN")
                .description("Administrator with full system access")
                .canManageUsers(true)
                .canManagePosts(true)
                .canManageEvents(true)
                .canManageGallery(true)
                .canComment(true)
                .canRegisterEvents(true)
                .build();
    }

    public static Role createStudentRole() {
        return Role.builder()
                .name("STUDENT")
                .description("Regular student account")
                .canManageUsers(false)
                .canManagePosts(false)
                .canManageEvents(false)
                .canManageGallery(false)
                .canComment(true)
                .canRegisterEvents(true)
                .build();
    }
}