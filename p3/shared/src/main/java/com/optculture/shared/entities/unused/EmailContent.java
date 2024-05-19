package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "email_content")
public class EmailContent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private java.lang.Long id;

    @Column(name = "name", length = 50)
    private String name;

    @Column(name = "html_content")
    private String htmlContent;

    @Column(name = "text_content")
    private String textContent;

    @Column(name = "campaign_id")
    private String campaignId;

}
