package com.optculture.shared.entities.loyalty;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "loyalty_transaction_child")
public class LoyaltyTransactionChild {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "trans_child_id")
    private java.lang.Long transChildId;

    @Column(name = "transaction_id")
    private java.lang.Long transactionId;

    @Column(name = "membership_number")
    private String membershipNumber;

    @Column(name = "membership_type")
    private String membershipType;

    @Column(name = "transaction_type")
    private String transactionType;

    @Column(name = "program_id")
    private java.lang.Long programId;

    @Column(name = "card_set_id")
    private java.lang.Long cardSetId;

    @Column(name = "tier_id")
    private java.lang.Long tierId;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "org_id")
    private java.lang.Long orgId;

    @Column(name = "entered_amount")
    private Double enteredAmount;

    @Column(name = "excluded_amount")
    private Double excludedAmount;

    @Column(name = "entered_amount_type")
    private String enteredAmountType;

    @Column(name = "points_difference")
    private String pointsDifference;

    @Column(name = "amount_difference")
    private String amountDifference;

    @Column(name = "gift_difference")
    private String giftDifference;

    @Column(name = "points_balance")
    private Double pointsBalance;

    @Column(name = "amount_balance")
    private Double amountBalance;

    @Column(name = "gift_balance")
    private Double giftBalance;

    @Column(name = "created_date")
    private java.time.LocalDateTime createdDate;

    @Column(name = "value_activation_date")
    private  java.time.LocalDateTime valueActivationDate;

    @Column(name = "earn_type")
    private String earnType;

    @Column(name = "earned_points")
    private Double earnedPoints;

    @Column(name = "earned_amount")
    private Double earnedAmount;

    @Column(name = "receipt_amount")
    private Double receiptAmount;

    @Column(name = "conversion_amt")
    private Double conversionAmt;

    @Column(name = "earn_status")
    private String earnStatus;

    @Column(name = "store_number")
    private String storeNumber;

    @Column(name = "subsidiary_number")
    private String subsidiaryNumber;

    @Column(name = "receipt_number")
    private String receiptNumber;

    @Column(name = "docsid")
    private String docSID;

    @Column(name = "source_type")
    private String sourceType;

    @Column(name = "description")
    private String description;

    @Column(name = "description2")
    private String description2;

    @Column(name = "contact_id")
    private java.lang.Long contactId;

    @Column(name = "loyalty_id")
    private java.lang.Long loyaltyId;

    @Column(name = "hold_points")
    private Double holdPoints;

    @Column(name = "hold_amount")
    private Double holdAmount;

    @Column(name = "redeemed_on")
    private java.lang.Long redeemedOn;

    @Column(name = "transfered_to")
    private java.lang.Long transferedTo;

    @Column(name = "transfered_on")
    private java.time.LocalDateTime transferedOn;

    @Column(name = "employee_id")
    private String employeeId;

    @Column(name = "terminal_id")
    private String terminalId;

    @Column(name = "reward_difference")
    private String rewardDifference;

    @Column(name = "reward_balance")
    private Double rewardBalance;

    @Column(name = "earned_reward")
    private Double earnedReward;

    @Column(name = "earned_rule")
    private String earnedRule;

    @Column(name = "special_reward_id")
    private java.lang.Long specialRewardId;

    @Column(name = "value_code")
    private String valueCode;

    @Column(name = "item_info")
    private String itemInfo;

    @Column(name = "issuance_amount")
    private Double issuanceAmount;

    @Column(name = "value_code_id")
    private java.lang.Long valueCodeId;

    @Column(name = "excluded_item_amount")
    private Double excludedItemAmount;

    @Column(name = "item_rewards_info")
    private String itemRewardsInfo;

}
