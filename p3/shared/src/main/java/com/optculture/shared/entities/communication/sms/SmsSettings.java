package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "org_sms_settings")
public class SmsSettings {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "setup_id")
    private java.lang.Long setupId;

    @Column(name = "type", length = 20)
    private String type;

    @Column(name = "keyword", length = 50)
    private String keyword;

    @Column(name = "auto_response")
    private String autoResponse;

    @Column(name = "valid_upto")
    private java.util.Calendar validUpto;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "start_from")
    private java.util.Calendar startFrom;

    @Column(name = "org_id")
    private java.lang.Long orgId;

    @Column(name = "list_id")
    private java.lang.Long listId;

    @Column(name = "enable_welcome_msg")
    private boolean enableWelcomeMessage;

    @Column(name = "welcome_msg")
    private String welcomeMessage;

    @Column(name = "msg_header", length = 50)
    private String messageHeader;

    @Column(name = "short_code", length = 50)
    private String shortCode;

    @Column(name = "optin_medium")
    private java.lang.Byte optInMedium;

    @Column(name = "enable")
    private boolean enable;

    @Column(name = "optin_missedcal_number", length = 50)
    private String optinMissedCalNumber;

    @Column(name = "sender_id", length = 50)
    private String senderId;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "modified_by")
    private java.lang.Long modifiedBy;

    @Column(name = "created_by")
    private java.lang.Long createdBy;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User userId;

}
