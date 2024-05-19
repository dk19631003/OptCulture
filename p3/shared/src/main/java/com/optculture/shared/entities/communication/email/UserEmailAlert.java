package com.optculture.shared.entities.communication.email;

import jakarta.persistence.*;
@Entity
@Table(name = "user_email_alert")
public class UserEmailAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_email_alert_id")
    private java.lang.Long userEmailAlertId;

    @Column(name = "user_id", length = 10)
    private java.lang.Long userId;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "created_by", length = 10)
    private java.lang.Long createdBy;

    @Column(name = "modified_by", length = 10)
    private java.lang.Long modifiedBy;

    @Column(name = "type", length = 60)
    private String type;

    @Column(name = "email_id", length = 500)
    private String emailId;

    @Column(name = "frequency", length = 60)
    private String frequency;

    @Column(name = "last_sent_on")
    private java.util.Calendar lastSentOn;

    @Column(name = "trigger_at", length = 60)
    private String triggerAt;

    @Column(name = "enabled")
    private java.lang.Boolean enabled;

}
