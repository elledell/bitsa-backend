package com.website.bitsa.controller.admin;



import com.website.bitsa.dto.response.ApiResponse;
import com.website.bitsa.model.Gallery;
import com.website.bitsa.service.GalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/gallery")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AdminGalleryController {

    private final GalleryService galleryService;

    @PostMapping
    public ResponseEntity<ApiResponse> uploadImage(
            @RequestBody Gallery gallery,
            Authentication authentication) {

        String userEmail = authentication.getName();
        Gallery savedImage = galleryService.uploadImage(gallery, userEmail);

        return new ResponseEntity<>(
                ApiResponse.success("Image uploaded successfully!", savedImage),
                HttpStatus.CREATED
        );
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse> updateImage(
            @PathVariable Long id,
            @RequestBody Gallery gallery) {

        Gallery updatedImage = galleryService.updateImage(id, gallery);
        return ResponseEntity.ok(ApiResponse.success("Image updated successfully!", updatedImage));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse> deleteImage(@PathVariable Long id) {
        galleryService.deleteImage(id);
        return ResponseEntity.ok(ApiResponse.success("Image deleted successfully!"));
    }

    @PutMapping("/{id}/toggle-publish")
    public ResponseEntity<ApiResponse> togglePublish(@PathVariable Long id) {
        Gallery gallery = galleryService.togglePublish(id);
        return ResponseEntity.ok(ApiResponse.success("Image publish status updated!", gallery));
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Admin Gallery API is working!");
    }
}