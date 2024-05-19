package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "sms_clicks")
public class SmsClicks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "click_id")
    private java.lang.Long clickId;

    @Column(name = "click_Url", length = 500)
    private java.lang.String clickUrl;

    @Column(name = "click_date")
    private java.util.Calendar clickDate;

    @Column(name = "sms_campUrl_id")
    private java.lang.Long smsCampUrlId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sent_id")
    private com.optculture.shared.entities.communication.sms.SmsCampaignSent sentId;

}
