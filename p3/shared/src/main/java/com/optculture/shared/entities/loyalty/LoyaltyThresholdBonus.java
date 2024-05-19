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
@Table(name = "loyalty_threshold_bonus")
public class LoyaltyThresholdBonus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "threshold_bonus_id")
    private java.lang.Long thresholdBonusId;

    @Column(name = "program_id")
    private java.lang.Long programId;

    @Column(name = "extra_bonus_type")
    private String extraBonusType;

    @Column(name = "extra_bonus_value")
    private double extraBonusValue;

    @Column(name = "earned_level_type")
    private String earnedLevelType;

    //changed datatype as unable to check !=null
    @Column(name = "earned_level_value")
    private Double earnedLevelValue;

    @Column(name = "threshold_limit")
    private double thresholdLimit;

    @Column(name = "is_recurring")
    private boolean recurring;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "registration_flag")
    private char registrationFlag;

    @Column(name = "email_temp_id")
    private java.lang.Long emailTempId;

    @Column(name = "sms_temp_id")
    private java.lang.Long smsTempId;

    @Column(name = "email_expiry_temp_id")
    private java.lang.Long emailExpiryTempId;

    @Column(name = "sms_expiry_temp_id")
    private java.lang.Long smsExpiryTempId;

    @Column(name = "bonus_expiry_date_type")
    private String bonusExpiryDateType;

    @Column(name = "bonus_expiry_date_value")
    private java.lang.Long bonusExpiryDateValue;

}
