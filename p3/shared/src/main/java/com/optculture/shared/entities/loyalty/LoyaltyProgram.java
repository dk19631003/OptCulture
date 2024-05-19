package com.optculture.shared.entities.loyalty;

import java.util.Date;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loyalty_program")
public class LoyaltyProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "program_id")
    private java.lang.Long programId;

    @Column(name = "program_name", length = 60)
    private String programName;

    @Column(name = "description")
    private String description;

    @Column(name = "tier_enable_flag")
    private char tierEnableFlag;

    @Column(name = "no_of_tiers")
    private int noOfTiers;

    @Column(name = "status", length = 60)
    private String status;

    @Column(name = "default_flag")
    private char defaultFlag;

    @Column(name = "unique_mobile_flag")
    private char uniqueMobileFlag;

    @Column(name = "reg_requisites")
    private String regRequisites;

    @Column(name = "user_id")
    private java.lang.Long userId;

    @Column(name = "org_id")
    private java.lang.Long orgId;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "created_by", length = 60)
    private String createdBy;

    @Column(name = "modified_by", length = 60)
    private String modifiedBy;

    @Column(name = "mbrship_expiry_on_level_upgd_flag")
    private char mbrshipExpiryOnLevelUpgdFlag;

    @Column(name = "draft_status")
    private String draftStatus;

    @Column(name = "reward_expiry_flag")
    private char rewardExpiryFlag;

    @Column(name = "membership_expiry_flag")
    private char membershipExpiryFlag;

    @Column(name = "reward_type")
    private String rewardType;

    @Column(name = "membership_type")
    private String membershipType;

    @Column(name = "gift_amount_expiry_flag")
    private char giftAmountExpiryFlag;

    @Column(name = "gift_amount_expiry_date_type", length = 60)
    private String giftAmountExpiryDateType;

    @Column(name = "gift_amount_expiry_date_value")
    private java.lang.Long giftAmountExpiryDateValue;

    @Column(name = "gift_membrshp_expiry_flag")
    private char giftMembrshpExpiryFlag;

    @Column(name = "gift_membrshp_expiry_date_type", length = 60)
    private String giftMembrshpExpiryDateType;

    @Column(name = "gift_membrshp_expiry_date_value")
    private java.lang.Long giftMembrshpExpiryDateValue;

    @Column(name = "validation_rule", length = 60)
    private String validationRule;

    @Column(name = "program_type", length = 60)
    private String programType;

    @Column(name = "unique_email_flag", length = 60)
    private char uniqueEmailFlag;

}
