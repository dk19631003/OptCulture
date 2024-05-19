package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "Autosms_url")
public class AutoSmsUrls {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "auto_SmsQueue_SentId")
    private java.lang.Long autoSmsQueueSentId;

    @Column(name = "originalUrl", length = 1000)
    private java.lang.String originalUrl;

    @Column(name = "shortUrl", length = 500)
    private java.lang.String shortUrl;

    @Column(name = "typeOfUrl", length = 10)
    private java.lang.String typeOfUrl;

    @Column(name = "shortCode", length = 20)
    private java.lang.String shortCode;

}
