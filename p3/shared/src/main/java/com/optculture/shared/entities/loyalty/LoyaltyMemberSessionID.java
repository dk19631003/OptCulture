package com.optculture.shared.entities.loyalty;

import jakarta.persistence.*;
@Entity
@Table(name = "loyalty_member_sessionID")
public class LoyaltyMemberSessionID {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "session_id")
    private String sessionID;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "org_id")
    private java.lang.Long orgID;

    @Column(name = "status")
    private String status;

    @Column(name = "device_id")
    private String deviceID;

}
