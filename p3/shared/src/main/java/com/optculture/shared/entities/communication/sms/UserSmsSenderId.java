package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "user_sms_sender_id")
public class UserSmsSenderId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "sender_id")
    private String senderId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "sms_type")
    private String smsType;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "modified_by")
    private java.lang.Long modifiedBy;

    @Column(name = "created_by")
    private java.lang.Long createdBy;

}
