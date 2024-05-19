package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "oc_sms_gateway")
public class SmsGateway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "country_name", length = 20)
    private String countryName;

    @Column(name = "gateway_name", length = 120)
    private String gatewayName;

    @Column(name = "user_id", length = 120)
    private String userId;

    @Column(name = "pwd", length = 20)
    private String pwd;

    @Column(name = "system_id", length = 120)
    private String systemId;

    @Column(name = "system_pwd", length = 20)
    private String systemPwd;

    @Column(name = "ip", length = 30)
    private String ip;

    @Column(name = "port", length = 20)
    private String port;

    @Column(name = "account_type", length = 10)
    private String accountType;

    @Column(name = "mode", length = 10)
    private String mode;

    @Column(name = "status", length = 20)
    private String status;

    @Column(name = "is_post_paid")
    private boolean postPaid;

    @Column(name = "pull_reports")
    private boolean pullReports;

    @Column(name = "created_by")
    private java.lang.Long createdBy;

    @Column(name = "modified_by")
    private java.lang.Long modifiedBy;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "pull_reports_URL")
    private String pullReportsURL;

    @Column(name = "server")
    private String server;

    @Column(name = "postpaid_bal_URL")
    private String postpaidBalURL;

    @Column(name = "system_type")
    private String systemType;

    @Column(name = "sender_id")
    private String senderId;

    @Column(name = "API_id")
    private String APIId;

    @Column(name = "principal_entity_id")
    private String principalEntityId;

    @Column(name = "enable_multi_thread_sub")
    private boolean enableMultiThreadSub;

    @Column(name = "enable_session_alive")
    private boolean enableSessionAlive;

    @Column(name = "two_way_sender_id")
    private String twoWaySenderID;

}
