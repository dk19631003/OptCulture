package com.optculture.shared.entities.promotion;

import jakarta.persistence.*;
@Entity
@Table(name = "referral_program")
public class ReferralProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "referral_id")
    private java.lang.Long referralId;

    @Column(name = "status")
    private String status;

    @Column(name = "name", length = 60)
    private String programName;

    @Column(name = "created_by")
    private Long userId;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "org_id")
    private Long orgId;

    @Column(name = "start_date")
    private java.util.Calendar startDate;

    @Column(name = "end_date")
    private java.util.Calendar endDate;

    @Column(name = "Discount_for_referrer_type")
    private String discountforReferrertype;

    @Column(name = "Discount_for_referrer_value")
    private Double discountforReferrervalue;

    @Column(name = "referee_MPV")
    private String refereeMPV;

    @Column(name = "no_limit")
    private Boolean noLimit;

    @Column(name = "reward_on_referral_type")
    private String rewardonReferraltype;

    @Column(name = "reward_on_referral_VC")
    private String rewardonReferralVC;

    @Column(name = "reward_on_referral_value")
    private String rewardonReferralValue;

    @Column(name = "coupon_id")
    private java.lang.Long couponId;

}
