package com.website.bitsa.service;


import com.website.bitsa.dto.request.CreateEventRequest;
import com.website.bitsa.dto.response.EventResponse;
import com.website.bitsa.exception.BadRequestException;
import com.website.bitsa.exception.ResourceNotFoundException;
import com.website.bitsa.model.Event;
import com.website.bitsa.model.EventRegistration;
import com.website.bitsa.model.EventType;
import com.website.bitsa.model.User;
import com.website.bitsa.repository.EventRegistrationRepository;
import com.website.bitsa.repository.EventRepository;
import com.website.bitsa.repository.EventTypeRepository;
import com.website.bitsa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventRegistrationRepository registrationRepository;
    private final EventTypeRepository eventTypeRepository;
    private final UserRepository userRepository;

    // ========== EVENT OPERATIONS ==========

    public List<EventResponse> getAllUpcomingEvents() {
        return eventRepository.findUpcomingEvents(LocalDateTime.now())
                .stream()
                .map(this::convertToEventResponse)
                .collect(Collectors.toList());
    }

    public List<EventResponse> getAllPastEvents() {
        return eventRepository.findPastEvents(LocalDateTime.now())
                .stream()
                .map(this::convertToEventResponse)
                .collect(Collectors.toList());
    }

    public EventResponse getEventBySlug(String slug) {
        Event event = eventRepository.findBySlugAndIsPublishedTrue(slug)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with slug: " + slug));

        event.incrementViewCount();
        eventRepository.save(event);

        return convertToEventResponse(event);
    }

    public EventResponse getEventById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found with id: " + id));
        return convertToEventResponse(event);
    }

    public List<EventResponse> getEventsByType(Long typeId) {
        return eventRepository.findUpcomingEventsByType(typeId, LocalDateTime.now())
                .stream()
                .map(this::convertToEventResponse)
                .collect(Collectors.toList());
    }

    public List<EventResponse> getFeaturedEvents() {
        return eventRepository.findFeaturedUpcomingEvents(LocalDateTime.now())
                .stream()
                .map(this::convertToEventResponse)
                .collect(Collectors.toList());
    }
    public List<EventResponse> getAllEventsAdmin() {
        return eventRepository.findAllByOrderByDateTimeDesc() // You might need to add this to Repository
                .stream()
                .map(this::convertToEventResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public EventResponse createEvent(CreateEventRequest request, String userEmail) {
        User creator = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        EventType eventType = eventTypeRepository.findById(request.getEventTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Event type not found"));

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .dateTime(request.getDateTime())
                .location(request.getLocation())
                .eventType(eventType)
                .creator(creator)
                .maxAttendees(request.getMaxAttendees())
                .durationMinutes(request.getDurationMinutes())
                .featuredImage(request.getFeaturedImage())
                .featuredImageAlt(request.getFeaturedImageAlt())
                .meetingLink(request.getMeetingLink())
                .requirements(request.getRequirements())
                .agenda(request.getAgenda())
                .registrationRequired(request.getRegistrationRequired())
                .isPublished(request.getIsPublished())
                .isFeatured(request.getIsFeatured())
                .currentAttendees(0)
                .build();

        Event savedEvent = eventRepository.save(event);

        // Update event type count
        eventType.incrementEventCount();
        eventTypeRepository.save(eventType);

        return convertToEventResponse(savedEvent);
    }

    @Transactional
    public EventResponse updateEvent(Long id, CreateEventRequest request) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        EventType eventType = eventTypeRepository.findById(request.getEventTypeId())
                .orElseThrow(() -> new ResourceNotFoundException("Event type not found"));

        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setDateTime(request.getDateTime());
        event.setLocation(request.getLocation());
        event.setEventType(eventType);
        event.setMaxAttendees(request.getMaxAttendees());
        event.setDurationMinutes(request.getDurationMinutes());
        event.setFeaturedImage(request.getFeaturedImage());
        event.setFeaturedImageAlt(request.getFeaturedImageAlt());
        event.setMeetingLink(request.getMeetingLink());
        event.setRequirements(request.getRequirements());
        event.setAgenda(request.getAgenda());
        event.setRegistrationRequired(request.getRegistrationRequired());
        event.setIsPublished(request.getIsPublished());
        event.setIsFeatured(request.getIsFeatured());

        Event updatedEvent = eventRepository.save(event);
        return convertToEventResponse(updatedEvent);
    }

    public void deleteEvent(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        // Decrement event type count
        EventType eventType = event.getEventType();
        eventType.decrementEventCount();
        eventTypeRepository.save(eventType);

        eventRepository.delete(event);
    }

    public EventResponse cancelEvent(Long id, String reason) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        event.cancel(reason);
        Event cancelledEvent = eventRepository.save(event);

        return convertToEventResponse(cancelledEvent);
    }

    // ========== REGISTRATION OPERATIONS ==========

    @Transactional
    public EventRegistration registerForEvent(Long eventId, String userEmail) {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found"));

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Check if already registered
        if (registrationRepository.existsByEventIdAndUserIdAndIsCancelledFalse(eventId, user.getId())) {
            throw new BadRequestException("You are already registered for this event");
        }

        // Check if registration is open
        if (!event.isRegistrationOpen()) {
            throw new BadRequestException("Registration is not open for this event");
        }

        // Check if event is full
        if (event.isFull()) {
            throw new BadRequestException("Event is full");
        }

        EventRegistration registration = EventRegistration.builder()
                .event(event)
                .user(user)
                .attendanceStatus("REGISTERED")
                .isCancelled(false)
                .isWaitlisted(false)
                .build();

        EventRegistration savedRegistration = registrationRepository.save(registration);

        // Update event attendee count
        event.incrementAttendeeCount();
        eventRepository.save(event);

        return savedRegistration;
    }

    public void cancelRegistration(Long eventId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        EventRegistration registration = registrationRepository.findByEventIdAndUserId(eventId, user.getId())
                .orElseThrow(() -> new ResourceNotFoundException("Registration not found"));

        registration.cancel("Cancelled by user");
        registrationRepository.save(registration);

        // Decrement event attendee count
        Event event = registration.getEvent();
        event.decrementAttendeeCount();
        eventRepository.save(event);
    }

    public List<EventRegistration> getEventRegistrations(Long eventId) {
        return registrationRepository.findByEventIdAndIsCancelledFalseOrderByRegistrationDateAsc(eventId);
    }

    public List<EventRegistration> getUserRegistrations(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return registrationRepository.findUpcomingRegistrationsByUser(user.getId());
    }

    // ========== HELPER METHODS ==========

    private EventResponse convertToEventResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .dateTime(event.getDateTime())
                .location(event.getLocation())
                .eventTypeName(event.getEventType().getName())
                .creatorName(event.getCreator().getName())
                .maxAttendees(event.getMaxAttendees())
                .currentAttendees(event.getCurrentAttendees())
                .availableSeats(event.getAvailableSeats())
                .featuredImage(event.getFeaturedImage())
                .isPublished(event.getIsPublished())
                .isFeatured(event.getIsFeatured())
                .isFull(event.isFull())
                .isRegistrationOpen(event.isRegistrationOpen())
                .isCancelled(event.getIsCancelled())
                .slug(event.getSlug())
                .createdAt(event.getCreatedAt())
                .build();
    }

    public long countUpcomingEvents() {
        return eventRepository.countUpcomingEvents(LocalDateTime.now());
    }
}