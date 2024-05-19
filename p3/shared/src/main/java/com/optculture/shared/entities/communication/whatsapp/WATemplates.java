package com.optculture.shared.entities.communication.whatsapp;

import jakarta.persistence.*;
@Entity
@Table(name = "wa_templates")
public class WATemplates {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "template_id")
    private java.lang.Long templateId;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "provider")
    private String provider;

    @Column(name = "type")
    private String type;

    @Column(name = "json_content")
    private String jsonContent;

    @Column(name = "placeholders")
    private String placeholders;

    @Column(name = "template_registered_id")
    private String templateRegisteredId;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "org_id")
    private java.lang.Long orgId;

    @Column(name = "status")
    private String status;

    @Column(name = "headers")
    private String headers;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "created_by")
    private java.util.Calendar createdBy;

    @Column(name = "modified_by")
    private java.util.Calendar modifiedBy;

}
