package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "sms_queue")
public class SmsQueue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "msg_type", length = 30)
    private String msgType;

    @Column(name = "contact_id")
    private java.lang.Long contactId;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "to_mobile_phone", length = 15)
    private String toMobilePhone;

    @Column(name = "message")
    private String message;

    @Column(name = "sent_date")
    private java.util.Calendar sentDate;

    @Column(name = "sender_id")
    private String senderId;

    @Column(name = "msg_Id")
    private String msgId;

    @Column(name = "dlr_Status")
    private String dlrStatus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User user;

}
