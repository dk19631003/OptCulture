package com.optculture.shared.entities.loyalty;

import jakarta.persistence.*;
@Entity
@Table(name = "loyalty_auto_comm")
public class LoyaltyAutoComm {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auto_comm_id")
    private java.lang.Long autoCommId;

    @Column(name = "program_id")
    private java.lang.Long programId;

    @Column(name = "reg_email_tmplt_id")
    private java.lang.Long regEmailTmpltId;

    @Column(name = "reg_sms_tmplt_id")
    private java.lang.Long regSmsTmpltId;

    @Column(name = "tier_upgd_email_tmplt_id")
    private java.lang.Long tierUpgdEmailTmpltId;

    @Column(name = "tier_upgd_sms_tmplt_id")
    private java.lang.Long tierUpgdSmsTmpltId;

    @Column(name = "thresh_bonus_email_tmplt_id")
    private java.lang.Long threshBonusEmailTmpltId;

    @Column(name = "thresh_bonus_sms_tmplt_id")
    private java.lang.Long threshBonusSmsTmpltId;

    @Column(name = "reward_expiry_email_tmplt_id")
    private java.lang.Long rewardExpiryEmailTmpltId;

    @Column(name = "reward_expiry_sms_tmplt_id")
    private java.lang.Long rewardExpirySmsTmpltId;

    @Column(name = "mbrship_expiry_email_tmplt_id")
    private java.lang.Long mbrshipExpiryEmailTmpltId;

    @Column(name = "mbrship_expiry_sms_tmplt_id")
    private java.lang.Long mbrshipExpirySmsTmpltId;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "created_by", length = 60)
    private String createdBy;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "modified_by", length = 60)
    private String modifiedBy;

    @Column(name = "gift_amt_expiry_email_tmplt_id")
    private java.lang.Long giftAmtExpiryEmailTmpltId;

    @Column(name = "gift_amt_expiry_sms_tmplt_id")
    private java.lang.Long giftAmtExpirySmsTmpltId;

    @Column(name = "gift_membrshp_expiry_email_tmplt_id")
    private java.lang.Long giftMembrshpExpiryEmailTmpltId;

    @Column(name = "gift_membrshp_expiry_sms_tmplt_id")
    private java.lang.Long giftMembrshpExpirySmsTmpltId;

    @Column(name = "gift_card_issuance_email_tmplt_id")
    private java.lang.Long giftCardIssuanceEmailTmpltId;

    @Column(name = "gift_card_issuance_sms_tmplt_id")
    private java.lang.Long giftCardIssuanceSmsTmpltId;

    @Column(name = "adjustment_auto_email_tmplt_id")
    private java.lang.Long adjustmentAutoEmailTmplId;

    @Column(name = "adjustment_auto_sms_tmplt_id")
    private java.lang.Long adjustmentAutoSmsTmplId;

    @Column(name = "issuance_auto_email_tmplt_id")
    private java.lang.Long issuanceAutoEmailTmplId;

    @Column(name = "issuance_auto_sms_tmplt_id")
    private java.lang.Long issuanceAutoSmsTmplId;

    @Column(name = "redemption_auto_email_tmplt_id")
    private java.lang.Long redemptionAutoEmailTmplId;

    @Column(name = "redemption_auto_sms_tmplt_id")
    private java.lang.Long redemptionAutoSmsTmplId;

    @Column(name = "otpmessage_auto_email_tmplt_id")
    private java.lang.Long otpMessageAutoEmailTmplId;

    @Column(name = "otpmessage_auto_sms_tmplt_id")
    private java.lang.Long otpMessageAutoSmsTmpltId;

    @Column(name = "redemptionotp_auto_email_tmplt_id")
    private java.lang.Long redemptionOtpAutoEmailTmplId;

    @Column(name = "redemptionotp_auto_sms_tmplt_id")
    private java.lang.Long redemptionOtpAutoSmsTmpltId;

}
