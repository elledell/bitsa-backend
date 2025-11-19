package com.website.bitsa.controller;



import com.website.bitsa.dto.request.ContactRequest;
import com.website.bitsa.dto.response.ApiResponse;
import com.website.bitsa.model.ContactMessage;
import com.website.bitsa.service.ContactService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/contact")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class ContactController {

    private final ContactService contactService;

    @PostMapping
    public ResponseEntity<ApiResponse> submitContactMessage(@Valid @RequestBody ContactRequest request) {
        ContactMessage message = contactService.submitContactMessage(request);
        return new ResponseEntity<>(
                ApiResponse.success("Message sent successfully! We'll get back to you soon.", message),
                HttpStatus.CREATED
        );
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Contact API is working!");
    }
}