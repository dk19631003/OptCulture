package com.optculture.shared.entities.loyalty;

import jakarta.persistence.*;
@Entity
@Table(name = "loyalty_threshold_alerts")
public class LoyaltyThresholdAlerts {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private java.lang.Long alertId;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "org_id")
    private java.lang.Long orgId;

    @Column(name = "lty_security_pwd")
    private String ltySecurityPwd;

    @Column(name = "enable_alerts")
    private char enableAlerts;

    @Column(name = "alert_email_id")
    private String alertEmailId;

    @Column(name = "alert_mobile_phn")
    private String alertMobilePhn;

    @Column(name = "count_type")
    private String countType;

    @Column(name = "count_value")
    private String countValue;

    @Column(name = "enroll_alert_last_sent_date")
    private java.util.Calendar enrollAlertLastSentDate;

    @Column(name = "webform_alert_last_sent_date")
    private java.util.Calendar webformAlertLastSentDate;

}
