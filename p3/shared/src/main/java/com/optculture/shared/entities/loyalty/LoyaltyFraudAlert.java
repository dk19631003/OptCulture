package com.optculture.shared.entities.loyalty;

import jakarta.persistence.*;
@Entity
@Table(name = "loyalty_fraud_alert")
public class LoyaltyFraudAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "fraud_alert_id")
    private java.lang.Long fraudAlertId;

    @Column(name = "rule_name")
    private String ruleName;

    @Column(name = "created__by_userId")
    private java.lang.Long createdByUserId;

    @Column(name = "modified_by_userId")
    private java.lang.Long modifiedByUserId;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "trx_rule")
    private String trxRule;

    @Column(name = "date_rule")
    private String dateRule;

    @Column(name = "email_id", length = 500)
    private String emailId;

    @Column(name = "frequency", length = 60)
    private String frequency;

    @Column(name = "last_sent_on")
    private java.util.Calendar lastSentOn;

    @Column(name = "trigger_at", length = 60)
    private String triggerAt;

    @Column(name = "send_email_enabled")
    private java.lang.Boolean enabled;

}
