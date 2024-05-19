package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "social_campaign_reports")
public class SocialCampaignReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "report_id")
    private java.lang.Long reportId;

    @Column(name = "campaign_id", length = 255)
    private java.lang.Long campaignId;

    @Column(name = "schedule_id", length = 255)
    private java.lang.Long scheduleId;

    @Column(name = "campaign_name", length = 255)
    private java.lang.String campaignName;

    @Column(name = "provider_type", length = 255)
    private java.lang.String providerType;

    @Column(name = "sent_date", length = 50)
    private java.util.Calendar sentDate;

    @Column(name = "campaign_status", length = 255)
    private java.lang.String campaignStatus;

    @Column(name = "provider_token", length = 255)
    private java.lang.String providerToken;

    @Column(name = "user_id", length = 255)
    private java.lang.Long userId;

}
