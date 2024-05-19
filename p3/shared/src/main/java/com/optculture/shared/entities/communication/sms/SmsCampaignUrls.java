package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "sms_campaign_urls")
public class SmsCampaignUrls {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "type", length = 10)
    private String typeOfUrl;

    @Column(name = "cr_id")
    private java.lang.Long crId;

    @Column(name = "original_url", length = 1000)
    private String originalUrl;

    @Column(name = "short_url", length = 500)
    private String shortUrl;

}
