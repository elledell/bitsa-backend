package com.website.bitsa.controller.admin;



import com.website.bitsa.dto.response.ApiResponse;
import com.website.bitsa.model.User;
import com.website.bitsa.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(user);
    }

    @GetMapping("/students")
    public ResponseEntity<List<User>> getAllStudents() {
        List<User> students = userService.getAllStudents();
        return ResponseEntity.ok(students);
    }

    @GetMapping("/admins")
    public ResponseEntity<List<User>> getAllAdmins() {
        List<User> admins = userService.getAllAdmins();
        return ResponseEntity.ok(admins);
    }

    @PutMapping("/{id}/deactivate")
    public ResponseEntity<ApiResponse> deactivateUser(@PathVariable Long id) {
        User user = userService.deactivateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deactivated successfully!", user));
    }

    @PutMapping("/{id}/activate")
    public ResponseEntity<ApiResponse> activateUser(@PathVariable Long id) {
        User user = userService.activateUser(id);
        return ResponseEntity.ok(ApiResponse.success("User activated successfully!", user));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.ok(ApiResponse.success("User deleted successfully!"));
    }

    @GetMapping("/stats")
    public ResponseEntity<ApiResponse> getUserStats() {
        long totalUsers = userService.countActiveUsers();
        long totalStudents = userService.countStudents();
        long totalAdmins = userService.countAdmins();

        return ResponseEntity.ok(ApiResponse.success("User statistics",
                new Object() {
                    public final long total = totalUsers;
                    public final long students = totalStudents;
                    public final long admins = totalAdmins;
                }
        ));
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Admin Users API is working!");
    }
}