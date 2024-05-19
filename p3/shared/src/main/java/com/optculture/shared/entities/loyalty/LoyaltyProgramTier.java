package com.optculture.shared.entities.loyalty;

import java.util.Calendar;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loyalty_program_tier")
public class LoyaltyProgramTier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tier_id")
    private Long tierId;

    @Column(name = "program_id")
    private Long programId;

    @Column(name = "tier_type", length = 60)
    private String tierType;

    @Column(name = "tier_name", length = 60)
    private String tierName;

    @Column(name = "earn_type", length = 60)
    private String earnType;

    @Column(name = "earn_value_type", length = 60)
    private String earnValueType;

    @Column(name = "earn_value")
    private Double earnValue;

    @Column(name = "earn_on_spent_amount")
    private Double earnOnSpentAmount;

    @Column(name = "issuance_chkenable")
    private Boolean issuanceChkEnable;

    @Column(name = "max_cap")
    private Double maxcap;

    @Column(name = "pts_active_date_type", length = 60)
    private String ptsActiveDateType;

    @Column(name = "pts_active_date_value")
    private Long ptsActiveDateValue;

    @Column(name = "convert_from_points")
    private Double convertFromPoints;

    @Column(name = "convert_to_amount")
    private Double convertToAmount;

    @Column(name = "conversion_type", length = 60)
    private String conversionType;

    @Column(name = "tier_upgd_constraint", length = 60)
    private String tierUpgdConstraint;

    @Column(name = "tier_upgd_constraint_value")
    private Double tierUpgdConstraintValue;

    @Column(name = "next_tier_type")
    private String nextTierType;

    @Column(name = "created_date")
    private Calendar createdDate;

    @Column(name = "created_by", length = 60)
    private String createdBy;

    @Column(name = "modified_date")
    private Calendar modifiedDate;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "reward_expiry_date_type", length = 60)
    private String rewardExpiryDateType;

    @Column(name = "reward_expiry_date_value")
    private Long rewardExpiryDateValue;

    @Column(name = "membership_expiry_date_type", length = 60)
    private String membershipExpiryDateType;

    @Column(name = "membership_expiry_date_value")
    private Long membershipExpiryDateValue;

    @Column(name = "activation_flag")
    private Character activationFlag;

    @Column(name = "tier_upgrade_cumulative_value")
    private Long tierUpgradeCumulativeValue;

    @Column(name = "rounding_type", length = 20)
    private String roundingType;

    @Column(name = "disallow_activate_after_stores")
    private String disallowActivateAfterStores;

    @Column(name = "activate_after_disable_all_store")
    private Boolean activateAfterDisableAllStore;

    @Column(name = "perk_limit_value")
    private Long perkLimitValue;

    @Column(name = "perk_limit_exp_type", length = 20)
    private String perkLimitExpType;

    @Column(name = "redemption_percentage_limit")
    private Double redemptionPercentageLimit;

    @Column(name = "redemption_value_limit")
    private Double redemptionValueLimit;

    @Column(name = "min_receipt_value")
    private Double minReceiptValue;

    @Column(name = "min_balance_value")
    private Double minBalanceValue;

    @Column(name = "crossover_bonus")
    private Double crossOverBonus;

    @Column(name = "redemption_otp_flag", length = 60)
    private Character redemptionOTPFlag;

    @Column(name = "otp_limit_amt")
    private Double otpLimitAmt;

    @Column(name = "consider_redeemed_amount_flag", length = 60)
    private Character considerRedeemedAmountFlag;

    @Column(name = "partial_reversal_flag")
    private Character partialReversalFlag;

    @Column(name = "multiple_tier_upgrd_rules")
    private String multipleTierUpgrdRules;

}
