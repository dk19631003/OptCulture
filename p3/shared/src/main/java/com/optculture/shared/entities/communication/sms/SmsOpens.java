package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "sms_opens")
public class SmsOpens {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "open_id")
    private java.lang.Long openId;

    @Column(name = "open_date")
    private java.util.Calendar openDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sent_id")
    private com.optculture.shared.entities.communication.sms.SmsCampaignSent sentId;

}
