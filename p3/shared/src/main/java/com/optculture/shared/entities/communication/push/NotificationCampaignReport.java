package com.optculture.shared.entities.communication.push;

import jakarta.persistence.*;
@Entity
@Table(name = "notification_campaign_report")
public class NotificationCampaignReport {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_cr_id")
    private java.lang.Long notificationCrId;

    @Column(name = "notification_campaign_name", length = 100)
    private String notificationCampaignName;

    @Column(name = "sent_date")
    private java.util.Calendar sentDate;

    @Column(name = "notification_Content")
    private String notificationContent;

    @Column(name = "notification_Header", length = 255)
    private String notificationHeaderContent;

    @Column(name = "notification_url", length = 100)
    private String notificationUrl;

    @Column(name = "notification_Logo_Image")
    private String notificationLogoImage;

    @Column(name = "notification_Banner_Image")
    private String notificationBannerImage;

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

    @Column(name = "source_type", length = 100)
    private String sourceType;

    @Column(name = "preference_count")
    private int preferenceCount;

    @Column(name = "credits_count")
    private int creditsCount;

    @Column(name = "user_id")
    private java.lang.Long userId;

}
