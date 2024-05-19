package com.optculture.shared.entities.config;

import jakarta.persistence.*;
@Entity
@Table(name = "template_category")
public class TemplateCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private java.lang.Long id;

    @Column(name = "category_name", length = 50)
    private String categoryName;

    @Column(name = "created_date")
    private java.util.Calendar cratedDate;

    @Column(name = "dir_name", length = 50)
    private String dirName;

}
