package com.optculture.shared.entities.communication.sms;

import jakarta.persistence.*;
@Entity
@Table(name = "user_sms_gateway")
public class UserSmsGateway {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "gateway_id")
    private java.lang.Long gatewayId;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "org_id")
    private java.lang.Long orgId;

    @Column(name = "account_type")
    private String accountType;

    @Column(name = "created_by")
    private java.lang.Long createdBy;

    @Column(name = "modified_by")
    private java.lang.Long modifiedBy;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "check_settings")
    private boolean checkSettings;

}
