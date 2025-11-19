package com.website.bitsa.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreatePostRequest {

    @NotBlank(message = "Title is required")
    @Size(max = 200, message = "Title must not exceed 200 characters")
    private String title;

    @NotBlank(message = "Content is required")
    private String content;

    @Size(max = 500, message = "Excerpt must not exceed 500 characters")
    private String excerpt;

    @NotNull(message = "Category ID is required")
    private Long categoryId;

    private String featuredImage;
    private String featuredImageAlt;
    private String metaTitle;
    private String metaDescription;
    private String metaKeywords;
    private String tags;
    private Boolean isPublished = false;
    private Boolean isFeatured = false;
    private Boolean commentsEnabled = true;
}
