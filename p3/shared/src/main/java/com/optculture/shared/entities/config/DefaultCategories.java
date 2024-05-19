package com.optculture.shared.entities.config;

import jakarta.persistence.*;
@Entity
@Table(name = "default_category")
public class DefaultCategories {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private java.lang.Long categoryId;

    @Column(name = "category_name", length = 50)
    private String categoryName;

    @Column(name = "description", length = 250)
    private String description;

}
