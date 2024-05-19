package com.optculture.shared.entities.communication.push;

import jakarta.persistence.*;
@Entity
@Table(name = "notificationClicks")
public class NotificationClicks {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "click_id")
    private java.lang.Long clickId;

    @Column(name = "notification_cr_id")
    private java.lang.Long notificationCrId;

    @Column(name = "sent_id")
    private java.lang.Long sentId;

    @Column(name = "click_Url", length = 255)
    private java.lang.String clickUrl;

    @Column(name = "click_date")
    private java.util.Calendar clickDate;

}
