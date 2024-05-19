package com.optculture.shared.entities.contact;

import java.util.Calendar;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import java.time.LocalDateTime;

import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contacts_loyalty")
public class ContactLoyalty {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "loyalty_id")
    private Long loyaltyId;

    @Column(name = "customer_id")
    private String customerId;

    @Column(name = "account_id")
    private Long accountId;

    @Column(name = "card_number")
    private String cardNumber;

    @Column(name = "email_id")
    private String emailId;

    @Column(name = "last_threshold")
    private String lastThreshold;

    @Column(name = "bonus_points_threshold")
    private Double bonusPointsThreshold;

    @Column(name = "bonus_currency_threshold")
    private Double bonusCurrencyThreshold;

    @Column(name = "bonus_points_iterator")
    private Double bonusPointsIterator;

    @Column(name = "bonus_currency_iterator")
    private Double bonusCurrencyIterator;

    @Column(name = "cummulative_purchase_value")
    private Double cummulativePurchaseValue;

    @Column(name = "cummulative_return_value")
    private Double cummulativeReturnValue;

    @Column(name = "amount_to_ignore")
    private Double amountToIgnore;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column(name = "membership_type")
    private String membershipType;

    @Column(name = "service_type")
    private String serviceType;

    @Column(name = "card_pin")
    private String cardPin;

    @Column(name = "location_id")
    private String locationId;

    @Column(name = "subsidiary_number")
    private String subsidiaryNumber;

    @Column(name = "pos_location_id")
    private String posStoreLocationId;

    @Column(name = "emp_id")
    private String empId;

    @Column(name = "total_loyalty_earned")
    private Double totalLoyaltyPointsEarned;

    @Column(name = "total_loyalty_redemption")
    private Double totalLoyaltyRedemption;

    @Column(name = "loyalty_balance")
    private Double loyaltyPointBalance;

    @Column(name = "value_code")
    private String valueCode;

    @Column(name = "total_giftcard_amount")
    private Double totalGiftcardAmount;

    @Column(name = "total_giftcard_redemption")
    private Double totalGiftcardRedemption;

    @Column(name = "giftcard_balance")
    private Double loyaltyCurrencyBalance;

    @Column(name = "total_gift_amount")
    private Double totalGiftAmount;

    @Column(name = "total_gift_redemption")
    private Double totalGiftRedemption;

    @Column(name = "gift_balance")
    private Double giftBalance;

    @Column(name = "holdpoints_balance")
    private Double holdpointsBalance;

    @Column(name = "holdamount_balance")
    private Double holdamountBalance;

    @Column(name = "optin_date")
    private Calendar optinDate;

    @Column(name = "last_redumption_date")
    private Calendar lastRedumptionDate;

    @Column(name = "card_type")
    private String cardType;

    @Column(name = "loyalty_type")
    private String loyaltyType;

    @Column(name = "last_feched_date")
    private Calendar lastFechedDate;

    //changed data type for Mail merge
    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "contact_loyalty_type")
    private String contactLoyaltyType;

    @Column(name = "source_type", length = 20)
    private String sourceType;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "org_id")
    private Long orgId;

    @Column(name = "is_registered")
    private Byte isRegistered;

    @Column(name = "mode")
    private String mode;

    @Column(name = "program_tier_id")
    private Long programTierId;

    //changed datatype for mail merge
    @Column(name = "tier_upgraded_date")
    private LocalDateTime tierUpgradedDate;

    @Column(name = "tier_upgrade_reason")
    private String tierUpgradeReason;

    @Column(name = "program_id")
    private Long programId;

    @Column(name = "membership_status")
    private String membershipStatus;

    @Column(name = "card_set_id")
    private Long cardSetId;

    @Column(name = "expired_points")
    private Long expiredPoints;

    @Column(name = "expired_reward_amount")
    private Double expiredRewardAmount;

    @Column(name = "expired_gift_amount")
    private Double expiredGiftAmount;

    @Column(name = "reward_flag")
    private String rewardFlag;

    @Column(name = "membership_pwd")
    private String membershipPwd;

    @Column(name = "membership_pwd_backup")
    private String membershipPwdBackup;

    @Column(name = "last_logged_in_time")
    private Calendar lastLoggedInTime;

    @Column(name = "fp_recognition_flag")
    private Boolean fpRecognitionFlag;

    @Column(name = "transfered_to")
    private Long transferedTo;

    @Column(name = "transfered_on")
    private Calendar transferedOn;

    @Column(name = "terminal_id")
    private String terminalId;


    @ManyToOne(fetch=FetchType.EAGER)
    @JoinColumn(name = "contact_id")
    private Contact contact;
    

   


}
