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
public class PostResponse {
    private Long id;
    private String title;
    private String content;
    private String excerpt;
    private String slug;
    private String authorName;
    private String categoryName;
    private String featuredImage;
    private Boolean isPublished;
    private Boolean isFeatured;
    private Integer viewCount;
    private Integer likeCount;
    private Integer commentCount;
    private Integer readingTimeMinutes;
    private String[] tags;
    private LocalDateTime publishedAt;
    private LocalDateTime createdAt;
}