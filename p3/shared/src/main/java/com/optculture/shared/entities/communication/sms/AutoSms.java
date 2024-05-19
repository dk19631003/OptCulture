package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "auto_sms")
public class AutoSms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auto_sms_id")
    private java.lang.Long autoSmsId;

    @Column(name = "auto_sms_type")
    private String autoSmsType;

    @Column(name = "template_name")
    private String templateName;

    @Column(name = "message_content")
    private String messageContent;

    @Column(name = "message_type")
    private String messageType;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "org_id")
    private java.lang.Long orgId;

    @Column(name = "created_by")
    private java.lang.Long createdBy;

    @Column(name = "modified_by")
    private java.lang.Long modifiedBy;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "sender_id")
    private String senderId;

    @Column(name = "status")
    private String status;

    @Column(name = "template_registered_id")
    private String templateRegisteredId;

}
