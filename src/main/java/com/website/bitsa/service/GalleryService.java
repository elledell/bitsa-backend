package com.website.bitsa.service;

import com.website.bitsa.exception.ResourceNotFoundException;
import com.website.bitsa.model.Gallery;
import com.website.bitsa.model.User;
import com.website.bitsa.repository.GalleryRepository;
import com.website.bitsa.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GalleryService {

    private final GalleryRepository galleryRepository;
    private final UserRepository userRepository;

    public List<Gallery> getAllPublishedImages() {
        return galleryRepository.findByIsPublishedTrueOrderByCreatedAtDesc();
    }

    public List<Gallery> getImagesByCategory(String category) {
        return galleryRepository.findByCategoryAndIsPublishedTrueOrderByCreatedAtDesc(category);
    }

    public List<Gallery> getFeaturedImages() {
        return galleryRepository.findByIsFeaturedTrueAndIsPublishedTrueOrderByDisplayOrderAsc();
    }

    public Gallery getImageById(Long id) {
        Gallery image = galleryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + id));

        image.incrementViewCount();
        galleryRepository.save(image);

        return image;
    }

    public List<String> getAllCategories() {
        return galleryRepository.findAllCategories();
    }

    @Transactional
    public Gallery uploadImage(Gallery gallery, String userEmail) {
        User uploader = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        gallery.setUploadedBy(uploader);
        return galleryRepository.save(gallery);
    }

    @Transactional
    public Gallery updateImage(Long id, Gallery updatedGallery) {
        Gallery gallery = galleryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

        gallery.setTitle(updatedGallery.getTitle());
        gallery.setDescription(updatedGallery.getDescription());
        gallery.setAltText(updatedGallery.getAltText());
        gallery.setCategory(updatedGallery.getCategory());
        gallery.setTags(updatedGallery.getTags());
        gallery.setIsFeatured(updatedGallery.getIsFeatured());
        gallery.setIsPublished(updatedGallery.getIsPublished());
        gallery.setDisplayOrder(updatedGallery.getDisplayOrder());

        return galleryRepository.save(gallery);
    }

    public void deleteImage(Long id) {
        Gallery gallery = galleryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));
        galleryRepository.delete(gallery);
    }

    public Gallery togglePublish(Long id) {
        Gallery gallery = galleryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found"));

        gallery.setIsPublished(!gallery.getIsPublished());
        return galleryRepository.save(gallery);
    }

    public List<Gallery> getMostViewedImages() {
        return galleryRepository.findTop10ByIsPublishedTrueOrderByViewCountDesc();
    }

    public List<Gallery> getMostLikedImages() {
        return galleryRepository.findTop10ByIsPublishedTrueOrderByLikeCountDesc();
    }

    public long countTotalImages() {
        return galleryRepository.countByIsPublishedTrue();
    }
}