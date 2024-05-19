package com.optculture.shared.entities.communication.push;

import jakarta.persistence.*;
@Entity
@Table(name = "Notification_campaign_sent")
public class NotificationCampaignSent {

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

    @Column(name = "status")
    private String status;

    @Column(name = "contact_id")
    private java.lang.Long contactId;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "instance_id")
    private String instanceId;

    @Column(name = "notification_read")
    private java.lang.Boolean notificationRead;

    @Column(name = "contactPhValStr")
    private String contactPhValStr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "notification_cr_id")
    private com.optculture.shared.entities.communication.push.NotificationCampaignReport notificationCampaignReport;

}
