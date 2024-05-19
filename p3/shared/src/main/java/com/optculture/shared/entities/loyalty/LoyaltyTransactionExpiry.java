package com.optculture.shared.entities.loyalty;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "loyalty_transaction_expiry")
public class LoyaltyTransactionExpiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trans_expiry_id")
    private java.lang.Long transExpiryId;

    @Column(name = "trans_child_id")
    private java.lang.Long transChildId;

    @Column(name = "membership_number")
    private String membershipNumber;

    @Column(name = "membership_type")
    private String membershipType;

    @Column(name = "reward_flag")
    private String rewardFlag;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "org_id")
    private java.lang.Long orgId;

    @Column(name = "program_id")
    private java.lang.Long programId;

    @Column(name = "tier_id")
    private java.lang.Long tierId;

    @Column(name = "expiry_points")
    private java.lang.Long expiryPoints;

    @Column(name = "expiry_reward")
    private java.lang.Long expiryReward;

    @Column(name = "expiry_amount")
    private Double expiryAmount;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "loyalty_id")
    private java.lang.Long loyaltyId;

    @Column(name = "transfered_to")
    private java.lang.Long transferedTo;

    @Column(name = "transfered_on")
    private LocalDateTime transferedOn;

    @Column(name = "value_code")
    private String valueCode;

    @Column(name = "special_reward_id")
    private java.lang.Long specialRewardId;

    @Column(name = "bonus_id")
    private java.lang.Long bonusId;

}
