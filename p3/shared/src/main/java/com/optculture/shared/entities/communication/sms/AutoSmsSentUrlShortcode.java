package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "Autosms_sent_url_short_code")
public class AutoSmsSentUrlShortcode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "short_Code_Id")
    private java.lang.Long shortCodeId;

    @Column(name = "auto_SmsQueue_SentId")
    private java.lang.Long autoSmsQueueSentId;

    @Column(name = "generatedShortCode", length = 20)
    private java.lang.String generatedShortCode;

    @Column(name = "originalShortCode", length = 20)
    private java.lang.String originalShortCode;

}
