package com.website.bitsa.dto.response;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EventRegistrationResponse {
    private Long id;
    private String eventTitle;
    private String userName;
    private String status;
    private LocalDateTime registrationDate;
}