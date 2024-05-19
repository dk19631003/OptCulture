package com.optculture.shared.entities.loyalty;

import jakarta.persistence.*;
@Entity
@Table(name = "special_rewards")
public class SpecialReward {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reward_id")
    private java.lang.Long rewardId;

    @Column(name = "reward_name")
    private String rewardName;

    @Column(name = "description")
    private String description;

    @Column(name = "reward_rule")
    private String rewardRule;

    @Column(name = "reward_type")
    private String rewardType;

    @Column(name = "associated_with_FBP")
    private boolean associatedWithFBP;

    @Column(name = "reward_value_code")
    private String rewardValueCode;

    @Column(name = "reward_value")
    private String rewardValue;

    @Column(name = "reward_expiry_type")
    private String rewardExpiryType;

    @Column(name = "reward_expiry_value")
    private String rewardExpiryValue;

    @Column(name = "org_id")
    private String orgId;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "status_special_reward")
    private String statusSpecialReward;

    @Column(name = "auto_comm_email")
    private String autoCommEmail;

    @Column(name = "auto_comm_sms")
    private String autoCommSms;

    @Column(name = "exclude_qty")
    private double excludeQty;

    @Column(name = "promo_code")
    private String promoCode;

    @Column(name = "promo_code_name")
    private String promoCodeName;

    @Column(name = "enable_return_on_current_rule")
    private boolean enableReturnOnCurrentRule;

    @Column(name = "deduct_item_price")
    private boolean deductItemPrice;

    @Column(name = "issuance_window")
    private String issuanceWindow;

/*
TODO: Convert Set
<set name="loyaltyPrograms" table="spreward_program">
           <key column="sprule_id" />
           <many-to-many class="org.mq.marketer.campaign.beans.LoyaltyProgram" column="program_id" />
       </set>
*/
}
