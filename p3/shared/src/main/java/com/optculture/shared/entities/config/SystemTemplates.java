package com.optculture.shared.entities.config;

import jakarta.persistence.*;
@Entity
@Table(name = "system_templates")
public class SystemTemplates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private java.lang.Long templateId;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "divisions")
    private String divisions;

    @Column(name = "html_text")
    private String htmlText;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "dir_name", length = 50)
    private String dirName;

    @Column(name = "json_text", length = 16777215)
    private String jsonText;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private com.optculture.shared.entities.config.TemplateCategory templateCategory;

}
