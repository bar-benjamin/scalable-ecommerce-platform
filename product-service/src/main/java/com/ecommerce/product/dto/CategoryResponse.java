package com.ecommerce.product.dto;

import com.ecommerce.product.domain.Category;

import java.time.Instant;

public class CategoryResponse {

    private final Long id;
    private final String name;
    private final String description;
    private final Instant created_at;

    private CategoryResponse(Long id, String name, String description, Instant created_at) {
        this.id          = id;
        this.name        = name;
        this.description = description;
        this.created_at  = created_at;
    }

    public static CategoryResponse from(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getCreatedAt());
    }

    public Long getId()           { return id; }
    public String getName()       { return name; }
    public String getDescription(){ return description; }
    public Instant getCreatedAt() { return created_at; }
}