package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "sms_bounces")
public class SmsBounces {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bounce_id")
    private java.lang.Long bounceId;

    @Column(name = "category")
    private String category;

    @Column(name = "mobile")
    private String mobile;

    @Column(name = "message")
    private String message;

    @Column(name = "date")
    private java.util.Calendar bouncedDate;

    @Column(name = "cr_id")
    private long crId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sent_id")
    private com.optculture.shared.entities.communication.sms.SmsCampaignSent sentId;

}
