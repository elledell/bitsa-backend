package com.website.bitsa.controller;



import com.website.bitsa.model.Gallery;
import com.website.bitsa.service.GalleryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/gallery")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class GalleryController {

    private final GalleryService galleryService;

    @GetMapping
    public ResponseEntity<List<Gallery>> getAllImages() {
        List<Gallery> images = galleryService.getAllPublishedImages();
        return ResponseEntity.ok(images);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Gallery> getImageById(@PathVariable Long id) {
        Gallery image = galleryService.getImageById(id);
        return ResponseEntity.ok(image);
    }

    @GetMapping("/category/{category}")
    public ResponseEntity<List<Gallery>> getImagesByCategory(@PathVariable String category) {
        List<Gallery> images = galleryService.getImagesByCategory(category);
        return ResponseEntity.ok(images);
    }

    @GetMapping("/featured")
    public ResponseEntity<List<Gallery>> getFeaturedImages() {
        List<Gallery> images = galleryService.getFeaturedImages();
        return ResponseEntity.ok(images);
    }

    @GetMapping("/categories")
    public ResponseEntity<List<String>> getAllCategories() {
        List<String> categories = galleryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/most-viewed")
    public ResponseEntity<List<Gallery>> getMostViewedImages() {
        List<Gallery> images = galleryService.getMostViewedImages();
        return ResponseEntity.ok(images);
    }

    @GetMapping("/most-liked")
    public ResponseEntity<List<Gallery>> getMostLikedImages() {
        List<Gallery> images = galleryService.getMostLikedImages();
        return ResponseEntity.ok(images);
    }

    @GetMapping("/test")
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Gallery API is working!");
    }
}