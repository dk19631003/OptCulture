package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "social_campaign_schedules")
public class SocialCampaignSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "schedule_id")
    private java.lang.Long scheduleId;

    @Column(name = "campaign_id")
    private java.lang.Long campaignId;

    @Column(name = "campaign_content", length = 2000)
    private java.lang.String campaignContent;

    @Column(name = "post_type")
    private java.lang.String postType;

    @Column(name = "url_links")
    private java.lang.String urlLinks;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "last_modified_date")
    private java.util.Calendar lastModifiedDate;

    @Column(name = "schedule_date")
    private java.util.Calendar scheduleDate;

    @Column(name = "schedule_status")
    private java.lang.String scheduleStatus;

    @Column(name = "failure_status", length = 255)
    private java.lang.String failureStatus;

}
