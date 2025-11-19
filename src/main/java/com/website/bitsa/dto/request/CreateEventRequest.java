package com.website.bitsa.dto.request;


import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateEventRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Date and time is required")
    @Future(message = "Event date must be in the future")
    private LocalDateTime dateTime;

    @NotBlank(message = "Location is required")
    @Size(max = 200, message = "Location must not exceed 200 characters")
    private String location;

    @NotNull(message = "Event type ID is required")
    private Long eventTypeId;

    private Integer maxAttendees;
    private Integer durationMinutes;
    private String featuredImage;
    private String featuredImageAlt;
    private String meetingLink;
    private String requirements;
    private String agenda;
    private Boolean registrationRequired = true;
    private Boolean isPublished = false;
    private Boolean isFeatured = false;
}