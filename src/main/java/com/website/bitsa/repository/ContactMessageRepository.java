package com.website.bitsa.repository;


import com.website.bitsa.model.ContactMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ContactMessageRepository extends JpaRepository<ContactMessage, Long> {

    // ========== STATUS QUERIES ==========

    /**
     * Find unread messages
     */
    List<ContactMessage> findByIsReadFalseOrderByCreatedAtDesc();

    /**
     * Find read messages
     */
    List<ContactMessage> findByIsReadTrueOrderByReadAtDesc();

    /**
     * Find pending messages (not read and not replied)
     */
    List<ContactMessage> findByIsReadFalseAndIsRepliedFalseOrderByCreatedAtDesc();

    /**
     * Find messages needing attention (read but not replied)
     */
    List<ContactMessage> findByIsReadTrueAndIsRepliedFalseOrderByReadAtDesc();

    /**
     * Find replied messages
     */
    List<ContactMessage> findByIsRepliedTrueOrderByRepliedAtDesc();

    // ========== SPAM QUERIES ==========

    /**
     * Find non-spam messages
     */
    List<ContactMessage> findByIsSpamFalseOrderByCreatedAtDesc();

    /**
     * Find spam messages
     */
    List<ContactMessage> findByIsSpamTrueOrderByCreatedAtDesc();
    // ========== CATEGORY QUERIES ==========

    /**
     * Find messages by category
     */
    List<ContactMessage> findByCategoryOrderByCreatedAtDesc(String category);

    /**
     * Find unread messages by category
     */
    List<ContactMessage> findByCategoryAndIsReadFalseOrderByCreatedAtDesc(String category);

    // ========== PRIORITY QUERIES ==========

    /**
     * Find messages by priority
     */
    List<ContactMessage> findByPriorityOrderByCreatedAtDesc(String priority);

    /**
     * Find urgent unread messages
     */
    List<ContactMessage> findByPriorityAndIsReadFalseOrderByCreatedAtDesc(String priority);

    /**
     * Find high priority pending messages
     */
    @Query("SELECT m FROM ContactMessage m WHERE m.priority IN ('HIGH', 'URGENT') AND " +
            "m.isRead = false AND m.isSpam = false ORDER BY m.createdAt DESC")
    List<ContactMessage> findHighPriorityPendingMessages();

    // ========== SENDER QUERIES ==========

    /**
     * Find messages by email
     */
    List<ContactMessage> findByEmailOrderByCreatedAtDesc(String email);

    /**
     * Count messages from email
     */
    long countByEmail(String email);

    /**
     * Find messages by sender name
     */
    List<ContactMessage> findByNameContainingIgnoreCaseOrderByCreatedAtDesc(String name);

    // ========== DATE RANGE QUERIES ==========

    /**
     * Find messages in date range
     */
    List<ContactMessage> findByCreatedAtBetweenOrderByCreatedAtDesc(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find recent messages (last 24 hours)
     */
    @Query("SELECT m FROM ContactMessage m WHERE m.createdAt > :since ORDER BY m.createdAt DESC")
    List<ContactMessage> findRecentMessages(@Param("since") LocalDateTime since);

    /**
     * Find messages from today
     */
    @Query("SELECT m FROM ContactMessage m WHERE DATE(m.createdAt) = CURRENT_DATE ORDER BY m.createdAt DESC")
    List<ContactMessage> findTodaysMessages();

    // ========== ADMIN QUERIES ==========

    /**
     * Find messages read by specific admin
     */
    List<ContactMessage> findByReadByOrderByReadAtDesc(Long adminId);

    /**
     * Find messages replied by specific admin
     */
    List<ContactMessage> findByRepliedByOrderByRepliedAtDesc(Long adminId);

    /**
     * Count messages handled by admin
     */
    long countByRepliedBy(Long adminId);

    // ========== STATISTICS QUERIES ==========

    /**
     * Count unread messages
     */
    long countByIsReadFalse();

    /**
     * Count pending messages
     */
    @Query("SELECT COUNT(m) FROM ContactMessage m WHERE m.isRead = false AND m.isReplied = false AND m.isSpam = false")
    long countPendingMessages();

    /**
     * Count messages by status
     */
    @Query("SELECT COUNT(m) FROM ContactMessage m WHERE m.isRead = :isRead AND m.isReplied = :isReplied AND m.isSpam = false")
    long countByStatus(@Param("isRead") boolean isRead, @Param("isReplied") boolean isReplied);

    /**
     * Count spam messages
     */
    long countByIsSpamTrue();

    /**
     * Count messages by category
     */
    long countByCategory(String category);

    /**
     * Get average response time (in hours)
     */
    @Query("SELECT AVG(TIMESTAMPDIFF(HOUR, m.createdAt, m.repliedAt)) FROM ContactMessage m WHERE m.isReplied = true")
    Double getAverageResponseTimeInHours();

    // ========== SEARCH QUERIES ==========

    /**
     * Search messages by subject
     */
    List<ContactMessage> findBySubjectContainingIgnoreCaseOrderByCreatedAtDesc(String keyword);

    /**
     * Search messages by content
     */
    @Query("SELECT m FROM ContactMessage m WHERE LOWER(m.message) LIKE LOWER(CONCAT('%', :keyword, '%')) ORDER BY m.createdAt DESC")
    List<ContactMessage> searchMessageContent(@Param("keyword") String keyword);

    /**
     * Search across multiple fields
     */
    @Query("SELECT m FROM ContactMessage m WHERE " +
            "LOWER(m.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.email) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.subject) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(m.message) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "ORDER BY m.createdAt DESC")
    List<ContactMessage> searchAllFields(@Param("keyword") String keyword);

    // ========== BULK OPERATIONS SUPPORT ==========

    /**
     * Find messages for bulk operations (by IDs)
     */
    List<ContactMessage> findByIdIn(List<Long> ids);

    /**
     * Find old messages (for cleanup/archiving)
     */
    @Query("SELECT m FROM ContactMessage m WHERE m.createdAt < :date AND m.isReplied = true")
    List<ContactMessage> findOldRepliedMessages(@Param("date") LocalDateTime date);
}
