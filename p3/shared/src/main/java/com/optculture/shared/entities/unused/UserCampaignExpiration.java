package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "user_campaign_expiration")
public class UserCampaignExpiration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "start_date")
    private java.util.Calendar startDate;

    @Column(name = "end_date")
    private java.util.Calendar endDate;

    @Column(name = "sent_on")
    private java.util.Calendar sentOn;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "campaign_id")
    private java.lang.Long campaignId;

    @Column(name = "status")
    private java.lang.String status;

    @Column(name = "to_email", length = 150)
    private java.lang.String toEmailId;

    @Column(name = "msg")
    private String msgContent;

}
