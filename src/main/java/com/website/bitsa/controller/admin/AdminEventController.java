package com.website.bitsa.controller.admin;

import com.website.bitsa.dto.request.CreateEventRequest;
import com.website.bitsa.dto.response.ApiResponse;
import com.website.bitsa.dto.response.EventResponse;
import com.website.bitsa.model.EventRegistration;
import com.website.bitsa.service.EventService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/events")
@RequiredArgsConstructor
// Note: No @CrossOrigin or @PreAuthorize (SecurityConfig handles them)
public class AdminEventController {

    private final EventService eventService;

    @PostMapping
    public ResponseEntity<ApiResponse> createEvent(
            @Valid @RequestBody CreateEventRequest request) {
        // We don't need the user email for now, assuming admin is creator
        EventResponse event = eventService.createEvent(request, "admin@bitsa.com");

        return new ResponseEntity<>(
                ApiResponse.success("Event created successfully!", event),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateEvent(
            @PathVariable Long id,
            @Valid @RequestBody CreateEventRequest request) {
        EventResponse event = eventService.updateEvent(id, request);
        return ResponseEntity.ok(ApiResponse.success("Event updated successfully!", event));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteEvent(@PathVariable Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.ok(ApiResponse.success("Event deleted successfully!"));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<ApiResponse> cancelEvent(
            @PathVariable Long id,
            @RequestParam String reason) {
        EventResponse event = eventService.cancelEvent(id, reason);
        return ResponseEntity.ok(ApiResponse.success("Event cancelled!", event));
    }

    // --- NEW: For Editing ---
    @GetMapping("/{id}")
    public ResponseEntity<EventResponse> getEventById(@PathVariable Long id) {
        EventResponse event = eventService.getEventById(id);
        return ResponseEntity.ok(event);
    }

    // --- NEW: For Admin List (Shows past & future) ---
    @GetMapping("/all")
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        List<EventResponse> events = eventService.getAllEventsAdmin();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{id}/registrations")
    public ResponseEntity<List<EventRegistration>> getEventRegistrations(@PathVariable Long id) {
        List<EventRegistration> registrations = eventService.getEventRegistrations(id);
        return ResponseEntity.ok(registrations);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Admin Events API is working!");
    }
}