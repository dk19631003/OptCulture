package com.optculture.shared.entities.communication.email;

import jakarta.persistence.*;
@Entity
@Table(name = "campaign_sent")
public class CampaignSent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sent_id")
    private java.lang.Long sentId;

    @Column(name = "email_id", length = 60)
    private String emailId;

    @Column(name = "opens")
    private int opens;

    @Column(name = "clicks")
    private int clicks;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "contact_id")
    private java.lang.Long contactId;

    @Column(name = "campaign_id")
    private java.lang.Long campaignId;

    @Column(name = "contactPhValStr")
    private String contactPhValStr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cr_id")
    private com.optculture.shared.entities.communication.email.CampaignReport campaignReport;

}
