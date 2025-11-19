package com.website.bitsa.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime dateTime;
    private String location;
    private String eventTypeName;
    private String creatorName;
    private Integer maxAttendees;
    private Integer currentAttendees;
    private Integer availableSeats;
    private String featuredImage;
    private Boolean isPublished;
    private Boolean isFeatured;
    private Boolean isFull;
    private Boolean isRegistrationOpen;
    private Boolean isCancelled;
    private String slug;
    private LocalDateTime createdAt;
}