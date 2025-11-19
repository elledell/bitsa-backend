package com.website.bitsa.controller;

import com.website.bitsa.dto.response.ApiResponse;
import com.website.bitsa.dto.response.EventResponse;
import com.website.bitsa.dto.response.EventRegistrationResponse; // <-- IMPORT THIS
import com.website.bitsa.model.EventRegistration;
import com.website.bitsa.service.EventService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/events")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173", allowCredentials = "true")
public class EventController {

    private final EventService eventService;

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllUpcomingEvents() {
        List<EventResponse> events = eventService.getAllUpcomingEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/past")
    public ResponseEntity<List<EventResponse>> getPastEvents() {
        List<EventResponse> events = eventService.getAllPastEvents();
        return ResponseEntity.ok(events);
    }

    @GetMapping("/{slug}")
    public ResponseEntity<EventResponse> getEventBySlug(@PathVariable String slug) {
        EventResponse event = eventService.getEventBySlug(slug);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/type/{typeId}")
    public ResponseEntity<List<EventResponse>> getEventsByType(@PathVariable Long typeId) {
        List<EventResponse> events = eventService.getEventsByType(typeId);
        return ResponseEntity.ok(events);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<EventResponse>> getFeaturedEvents() {
        List<EventResponse> events = eventService.getFeaturedEvents();
        return ResponseEntity.ok(events);
    }

    @PostMapping("/{eventId}/register")
    public ResponseEntity<ApiResponse> registerForEvent(
            @PathVariable Long eventId,
            Authentication authentication) {

        String userEmail = authentication.getName();

        // 1. Perform the registration (returns Entity)
        EventRegistration registration = eventService.registerForEvent(eventId, userEmail);

        // 2. Convert to Safe DTO (Breaks the Infinite Loop)
        EventRegistrationResponse responseDto = EventRegistrationResponse.builder()
                .id(registration.getId())
                .eventTitle(registration.getEvent().getTitle())
                .userName(registration.getUser().getName())
                .status(registration.getAttendanceStatus())
                .registrationDate(registration.getRegistrationDate())
                .build();

        return new ResponseEntity<>(
                ApiResponse.success("Successfully registered for event!", responseDto),
                HttpStatus.CREATED
        );
    }

    @DeleteMapping("/{eventId}/register")
    public ResponseEntity<ApiResponse> cancelRegistration(
            @PathVariable Long eventId,
            Authentication authentication) {

        String userEmail = authentication.getName();
        eventService.cancelRegistration(eventId, userEmail);

        return ResponseEntity.ok(ApiResponse.success("Registration cancelled successfully"));
    }

    @GetMapping("/my-registrations")
    public ResponseEntity<List<EventRegistrationResponse>> getMyRegistrations(Authentication authentication) {
        String userEmail = authentication.getName();
        List<EventRegistration> registrations = eventService.getUserRegistrations(userEmail);

        // Convert list of Entities to list of DTOs to prevent crash
        List<EventRegistrationResponse> responseList = registrations.stream()
                .map(reg -> EventRegistrationResponse.builder()
                        .id(reg.getId())
                        .eventTitle(reg.getEvent().getTitle())
                        .userName(reg.getUser().getName())
                        .status(reg.getAttendanceStatus())
                        .registrationDate(reg.getRegistrationDate())
                        .build())
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Events API is working!");
    }
}