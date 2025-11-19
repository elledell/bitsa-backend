package com.website.bitsa.controller.admin;


import com.website.bitsa.dto.response.ApiResponse;
import com.website.bitsa.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminDashboardController {

    private final UserService userService;
    private final BlogService blogService;
    private final EventService eventService;
    private final GalleryService galleryService;
    private final ContactService contactService;

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();

        try {
            // User Statistics
            stats.put("totalUsers", userService.countActiveUsers());
            stats.put("totalStudents", userService.countStudents());
            stats.put("totalAdmins", userService.countAdmins());

            // Blog Statistics
            stats.put("totalPosts", blogService.countTotalPosts());
            stats.put("pendingComments", blogService.getPendingComments().size());

            // Event Statistics
            stats.put("upcomingEvents", eventService.countUpcomingEvents());

            // Gallery Statistics
            stats.put("totalImages", galleryService.countTotalImages());

            // Contact Statistics
            stats.put("unreadMessages", contactService.countUnreadMessages());
            stats.put("pendingMessages", contactService.countPendingMessages());

            return ResponseEntity.ok(ApiResponse.success("Dashboard statistics", stats));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Error loading statistics: " + e.getMessage()));
        }
    }

    @GetMapping("/recent-activity")
    public ResponseEntity<ApiResponse> getRecentActivity() {
        Map<String, Object> activity = new HashMap<>();

        try {
            // Recent contact messages (last 24 hours)
            activity.put("recentMessages", contactService.getRecentMessages());

            // Recent pending comments
            activity.put("pendingComments", blogService.getPendingComments());

            return ResponseEntity.ok(ApiResponse.success("Recent activity", activity));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Error loading activity: " + e.getMessage()));
        }
    }

    @GetMapping("/analytics")
    public ResponseEntity<ApiResponse> getAnalytics() {
        Map<String, Object> analytics = new HashMap<>();

        try {
            // Calculate growth metrics
            analytics.put("userGrowth", userService.countActiveUsers());
            analytics.put("contentGrowth", blogService.countTotalPosts());
            analytics.put("eventEngagement", eventService.countUpcomingEvents());

            return ResponseEntity.ok(ApiResponse.success("Analytics data", analytics));
        } catch (Exception e) {
            return ResponseEntity.ok(ApiResponse.error("Error loading analytics: " + e.getMessage()));
        }
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Admin Dashboard API is working!");
    }
}