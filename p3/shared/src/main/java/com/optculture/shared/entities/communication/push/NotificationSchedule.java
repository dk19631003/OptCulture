package com.optculture.shared.entities.communication.push;

import jakarta.persistence.*;
@Entity
@Table(name = "Notification_schedule")
public class NotificationSchedule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_cs_id")
    private java.lang.Long notificationCsId;

    @Column(name = "notification_Id")
    private java.lang.Long notificationId;

    @Column(name = "notification_cr_id")
    private java.lang.Long notificationCrId;

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
