package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "auto_sms_queue")
public class AutoSmsQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "message")
    private String message;

    @Column(name = "type", length = 30)
    private String type;

    @Column(name = "status")
    private String status;

    @Column(name = "to_Mobile_No")
    private String toMobileNo;

    @Column(name = "account_type", length = 30)
    private String accountType;

    @Column(name = "sender_id")
    private String senderId;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "sent_date")
    private java.util.Calendar sentDate;

    @Column(name = "contact_id")
    private java.lang.Long contactId;

    @Column(name = "message_id", length = 100)
    private String messageId;

    @Column(name = "dlr_status")
    private String dlrStatus;

    @Column(name = "loyalty_id")
    private java.lang.Long loyaltyId;

    @Column(name = "template_Id")
    private java.lang.Long templateId;

    @Column(name = "clicks")
    private int clicks;

    @Column(name = "template_registered_id")
    private String templateRegisteredId;

}
