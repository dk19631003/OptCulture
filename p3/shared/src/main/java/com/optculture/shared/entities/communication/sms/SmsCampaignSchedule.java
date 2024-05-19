package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "SMS_campaign_schedule")
public class SmsCampaignSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sms_cs_id")
    private java.lang.Long smsCsId;

    @Column(name = "cr_id")
    private java.lang.Long crId;

    @Column(name = "sms_campaign_id")
    private java.lang.Long smsCampaignId;

    @Column(name = "parent_id")
    private java.lang.Long parentId;

    @Column(name = "criteria")
    private byte criteria;

    @Column(name = "scheduled_date")
    private java.util.Calendar scheduledDate;

    @Column(name = "status")
    private byte status;

    @Column(name = "resend_level")
    private byte resendLevel;

    @Column(name = "user_id")
    private java.lang.Long userId;

}
