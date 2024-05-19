package com.optculture.shared.entities.contact;

import jakarta.persistence.*;
@Entity
@Table(name = "contact_specific_date_events")
public class ContactSpecificDateEvents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "event_id")
    private java.lang.Long eventId;

    @Column(name = "event_trigger_id")
    private java.lang.Long eventTriggerId;

    @Column(name = "trigger_type")
    private java.lang.Integer triggerType;

    @Column(name = "created_time")
    private java.util.Calendar createdTime;

    @Column(name = "event_time")
    private java.util.Calendar eventTime;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "email_status")
    private byte emailStatus;

    @Column(name = "sms_status")
    private byte smsStatus;

    @Column(name = "event_category")
    private String eventCategory;

    @Column(name = "source_id")
    private java.lang.Long sourceId;

    @Column(name = "contact_id")
    private java.lang.Long contactId;

    @Column(name = "tr_condition")
    private String triggerCondition;

    @Column(name = "campaign_sent_date")
    private java.util.Calendar campaignSentDate;

    @Column(name = "sms_sent_date")
    private java.util.Calendar smsSentDate;

    @Column(name = "camp_sent_id")
    private java.lang.Long campSentId;

    @Column(name = "camp_cr_id")
    private java.lang.Long campCrId;

    @Column(name = "sms_sent_id")
    private java.lang.Long smsSentId;

    @Column(name = "sms_cr_id")
    private java.lang.Long smsCrId;

}
