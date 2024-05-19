package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "sms_campaign_report")
public class SmsCampaignReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sms_cr_id")
    private java.lang.Long smsCrId;

    @Column(name = "sms_campaign_name", length = 100)
    private String smsCampaignName;

    @Column(name = "sent_date")
    private java.util.Calendar sentDate;

    @Column(name = "content")
    private String content;

    @Column(name = "configured")
    private long configured;

    @Column(name = "suppressed_count")
    private long suppressedCount;

    @Column(name = "sent")
    private long sent;

    @Column(name = "opens")
    private int opens;

    @Column(name = "clicks")
    private int clicks;

    @Column(name = "unsubscribes")
    private int unsubscribes;

    @Column(name = "bounces")
    private int bounces;

    @Column(name = "status", length = 100)
    private String status;

    @Column(name = "source_type")
    private String sourceType;

    @Column(name = "preference_count")
    private int preferenceCount;

    @Column(name = "credits_count")
    private int creditsCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User user;

}
