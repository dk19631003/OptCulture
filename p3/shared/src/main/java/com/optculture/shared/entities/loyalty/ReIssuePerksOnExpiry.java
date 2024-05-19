package com.optculture.shared.entities.loyalty;

import jakarta.persistence.*;
@Entity
@Table(name = "reissue_perks_on_expiry")
public class ReIssuePerksOnExpiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reissue_perk_id")
    private java.lang.Long reIssuePerkId;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "program_id")
    private java.lang.Long programId;

    @Column(name = "loyalty_id")
    private java.lang.Long loyaltyId;

    @Column(name = "tier_id")
    private java.lang.Long tierId;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "value_code", length = 20)
    private String valueCode;

    @Column(name = "transaction_id")
    private java.lang.Long transactionId;

    @Column(name = "status", length = 20)
    private String status;

}
