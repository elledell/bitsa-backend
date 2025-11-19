package com.website.bitsa.controller.admin;


import com.website.bitsa.dto.response.ApiResponse;
import com.website.bitsa.model.ContactMessage;
import com.website.bitsa.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/contact")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@CrossOrigin(origins = "http://localhost:5173")
public class AdminContactController {

    private final ContactService contactService;

    @GetMapping
    public ResponseEntity<List<ContactMessage>> getAllMessages() {
        List<ContactMessage> messages = contactService.getAllMessages();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/unread")
    public ResponseEntity<List<ContactMessage>> getUnreadMessages() {
        List<ContactMessage> messages = contactService.getUnreadMessages();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/pending")
    public ResponseEntity<List<ContactMessage>> getPendingMessages() {
        List<ContactMessage> messages = contactService.getPendingMessages();
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ContactMessage> getMessageById(@PathVariable Long id) {
        ContactMessage message = contactService.getMessageById(id);
        return ResponseEntity.ok(message);
    }

    @PutMapping("/{id}/mark-read")
    public ResponseEntity<ApiResponse> markAsRead(
            @PathVariable Long id,
            Authentication authentication) {

        // You'll need to implement getting admin ID from email
        ContactMessage message = contactService.markAsRead(id, 1L); // Replace with actual admin ID
        return ResponseEntity.ok(ApiResponse.success("Message marked as read", message));
    }

    @PostMapping("/{id}/reply")
    public ResponseEntity<ApiResponse> replyToMessage(
            @PathVariable Long id,
            @RequestParam String reply,
            Authentication authentication) {

        ContactMessage message = contactService.replyToMessage(id, reply, 1L); // Replace with actual admin ID
        return ResponseEntity.ok(ApiResponse.success("Reply sent successfully!", message));
    }

    @PutMapping("/{id}/spam")
    public ResponseEntity<ApiResponse> markAsSpam(@PathVariable Long id) {
        ContactMessage message = contactService.markAsSpam(id);
        return ResponseEntity.ok(ApiResponse.success("Message marked as spam", message));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteMessage(@PathVariable Long id) {
        contactService.deleteMessage(id);
        return ResponseEntity.ok(ApiResponse.success("Message deleted successfully!"));
    }

    @GetMapping("/search")
    public ResponseEntity<List<ContactMessage>> searchMessages(@RequestParam String keyword) {
        List<ContactMessage> messages = contactService.searchMessages(keyword);
        return ResponseEntity.ok(messages);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Admin Contact API is working!");
    }
}