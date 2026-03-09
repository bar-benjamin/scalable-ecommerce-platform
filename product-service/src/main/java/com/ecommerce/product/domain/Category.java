package com.ecommerce.product.domain;

import jakarta.persistence.*;
import java.time.Instant;

@Entity
@Table(name = "categories", schema = "product_schema")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;

    @Column(nullable = false, updatable = false)
    private Instant created_at;

    protected Category() {}

    private Category(Builder builder) {
        this.name        = builder.name;
        this.description = builder.description;
        this.created_at  = Instant.now();
    }

    public Long getId()          { return id; }
    public String getName()      { return name; }
    public String getDescription(){ return description; }
    public Instant getCreatedAt(){ return created_at; }

    public void setName(String name)              { this.name = name; }
    public void setDescription(String description){ this.description = description; }

    public static Builder builder() { return new Builder(); }

    public static final class Builder {
        private String name;
        private String description;

        public Builder name(String name)              { this.name = name; return this; }
        public Builder description(String description){ this.description = description; return this; }
        public Category build()                       { return new Category(this); }
    }
}