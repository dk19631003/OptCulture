package com.optculture.shared.entities.communication.email;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "campaign_schedule")
public class CampaignSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cs_id")
    private java.lang.Long csId;

    @Column(name = "cr_id")
    private java.lang.Long crId;

    @Column(name = "campaign_id")
    private java.lang.Long campaignId;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id")
    private com.optculture.shared.entities.unused.EmailContent emailContent;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private com.optculture.shared.entities.org.User user;

}
