package com.optculture.shared.entities.config;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "faq")
public class Faq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "faq_id")
    private java.lang.Long faqId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "org_id")
    private java.lang.Long orgId;

    @Column(name = "language")
    private String language;

    @Column(name = "faq_content")
    private String faqContent;

    @Column(name = "terms_and_condition")
    private String termsAndCondition;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

}
