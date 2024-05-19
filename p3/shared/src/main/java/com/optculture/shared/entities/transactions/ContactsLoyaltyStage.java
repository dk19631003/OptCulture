package com.optculture.shared.entities.transactions;

import jakarta.persistence.*;
@Entity
@Table(name = "contacts_loyalty_stage")
public class ContactsLoyaltyStage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loyalty_stage_id")
    private java.lang.Long loyaltyStageId;

    @Column(name = "contact_id")
    private long contactId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "email_id")
    private String emailId;

    @Column(name = "phone_number")
    private String phoneNumber;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "card_pin")
    private String cardPin;

    @Column(name = "location_id")
    private String locationId;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "status")
    private String status;

    @Column(name = "trx_id")
    private long trxId;

    @Column(name = "service_type")
    private String serviceType;

    @Column(name = "req_type")
    private String reqType;

}
