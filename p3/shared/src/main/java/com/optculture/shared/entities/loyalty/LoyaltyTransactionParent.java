package com.optculture.shared.entities.loyalty;

import jakarta.persistence.*;
@Entity
@Table(name = "loyalty_transaction_parent")
public class LoyaltyTransactionParent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "transaction_id")
    private java.lang.Long transactionId;

    @Column(name = "request_id")
    private String requestId;

    @Column(name = "request_date")
    private String requestDate;

    @Column(name = "pcflag")
    private String pcFlag;

    @Column(name = "membership_number")
    private String membershipNumber;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "user_name")
    private String userName;

    @Column(name = "status")
    private String status;

    @Column(name = "error_message")
    private String errorMessage;

}
