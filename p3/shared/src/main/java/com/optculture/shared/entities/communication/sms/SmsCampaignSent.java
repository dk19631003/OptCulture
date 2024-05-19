package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "sms_campaign_sent")
public class SmsCampaignSent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sent_id")
    private java.lang.Long sentId;

    @Column(name = "mobile_number", length = 60)
    private String mobileNumber;

    @Column(name = "opens")
    private int opens;

    @Column(name = "clicks")
    private int clicks;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "contact_id")
    private java.lang.Long contactId;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "api_msg_id")
    private String apiMsgId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sms_cr_id")
    private com.optculture.shared.entities.communication.sms.SmsCampaignReport smsCampaignReport;

}
