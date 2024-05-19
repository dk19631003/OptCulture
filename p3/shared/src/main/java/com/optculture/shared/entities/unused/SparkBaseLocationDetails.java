package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "sparkbase_location_details")
public class SparkBaseLocationDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sparkbase_location_details_id")
    private java.lang.Long sparkBaseLocationDetails_id;

    @Column(name = "batch_id")
    private String batchId;

    @Column(name = "client_id")
    private String clientId;

    @Column(name = "external_id")
    private String externalId;

    @Column(name = "initiator_id")
    private String initiatorId;

    @Column(name = "initiator_password")
    private String initiatorPassword;

    @Column(name = "initiator_type")
    private String initiatorType;

    @Column(name = "integration_password")
    private String integrationPassword;

    @Column(name = "integration_username")
    private String integrationUserName;

    @Column(name = "locale_id")
    private String localeId;

    @Column(name = "location_id")
    private String locationId;

    @Column(name = "transaction_location_id")
    private String transactionLocationId;

    @Column(name = "system_id")
    private String systemId;

    @Column(name = "terminal_id")
    private String terminalId;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "enabled")
    private boolean enabled;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "conversion_type", length = 60)
    private String conversionType;

    @Column(name = "convert_to_amount")
    private long convertToAmount;

    @Column(name = "convert_from_points")
    private long convertFromPoints;

    @Column(name = "earn_on_spent_amount")
    private long earnOnSpentAmount;

    @Column(name = "earn_value")
    private long earnValue;

    @Column(name = "earn_type")
    private String earnType;

    @Column(name = "earn_value_type")
    private String earnValueType;

    @Column(name = "last_fetched_time")
    private java.util.Calendar lastFetchedTime;

    @Column(name = "fetch_freq_in_min")
    private long fetchFreqInMin;

    @Column(name = "enable_alerts")
    private boolean enableAlerts;

    @Column(name = "email_alerts")
    private boolean emailAlerts;

    @Column(name = "sms_alerts")
    private boolean smsAlerts;

    @Column(name = "count_type")
    private String countType;

    @Column(name = "count_value")
    private String countValue;

    @Column(name = "loyalty_alerts_sent_date")
    private java.util.Calendar loyaltyAlertsSentDate;

    @Column(name = "org_user_id")
    private java.lang.Long orgUserId;

    @Column(name = "org_user_name")
    private String orgUserName;

    @Column(name = "is_mobile_unique")
    private boolean mobileUnique;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_organization")
    private com.optculture.shared.entities.org.UserOrganization userOrganization;

}
