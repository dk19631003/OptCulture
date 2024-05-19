package com.optculture.shared.entities.communication.whatsapp;

import jakarta.persistence.*;
@Entity
@Table(name = "user_wa_configs")
public class UserWAConfigs {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "org_id")
    private java.lang.Long orgId;

    @Column(name = "provider")
    private String provider;

    @Column(name = "wa_api_endpoint")
    private String waAPIEndPoint;

    @Column(name = "access_token")
    private String accessToken;

    @Column(name = "from_id")
    private String fromId;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

}
