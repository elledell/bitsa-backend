package com.website.bitsa.repository;

import com.website.bitsa.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    // ========== AUTHENTICATION QUERIES ==========

    /**
     * Find user by email (for login)
     */
    Optional<User> findByEmail(String email);

    /**
     * Find user by student ID
     */
    Optional<User> findByStudentId(String studentId);

    /**
     * Check if email exists (for registration validation)
     */
    boolean existsByEmail(String email);

    /**
     * Check if student ID exists
     */
    boolean existsByStudentId(String studentId);

    // ========== ROLE-BASED QUERIES ==========

    /**
     * Find all users by role
     */
    List<User> findByRoleId(Long roleId);

    /**
     * Find all users by role name
     */
    @Query("SELECT u FROM User u WHERE u.role.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);

    /**
     * Find all active users
     */
    List<User> findByIsActiveTrue();

    /**
     * Find all admins
     */
    @Query("SELECT u FROM User u WHERE u.role.name = 'ADMIN' AND u.isActive = true")
    List<User> findAllAdmins();

    /**
     * Find all students
     */
    @Query("SELECT u FROM User u WHERE u.role.name = 'STUDENT' AND u.isActive = true")
    List<User> findAllStudents();

    // ========== SEARCH QUERIES ==========

    /**
     * Search users by name (case-insensitive)
     */
    List<User> findByNameContainingIgnoreCase(String name);

    /**
     * Find users by course
     */
    List<User> findByCourse(String course);

    /**
     * Find users by year
     */
    List<User> findByYear(Integer year);

    /**
     * Find users by course and year
     */
    List<User> findByCourseAndYear(String course, Integer year);

    // ========== STATISTICS QUERIES ==========

    /**
     * Count total users
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.isActive = true")
    long countActiveUsers();

    /**
     * Count users by role
     */
    @Query("SELECT COUNT(u) FROM User u WHERE u.role.name = :roleName AND u.isActive = true")
    long countByRoleName(@Param("roleName") String roleName);

    /**
     * Find recently registered users
     */
    List<User> findTop10ByOrderByCreatedAtDesc();

    /**
     * Find users who registered after a date
     */
    List<User> findByCreatedAtAfter(LocalDateTime date);

    // ========== EMAIL VERIFICATION ==========

    /**
     * Find users with unverified email
     */
    List<User> findByIsEmailVerifiedFalse();

    /**
     * Count verified users
     */
    long countByIsEmailVerifiedTrue();
}
