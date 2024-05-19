package com.optculture.shared.entities.promotion;

import jakarta.persistence.*;
@Entity
@Table(name = "reward_on_referral_type")
public class RewardReferraltype {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ref_id")
    private java.lang.Long refId;

    @Column(name = "reward_on_referral_type")
    private String rewardonReferraltype;

    @Column(name = "reward_on_referral_repeats")
    private String rewardonReferralRepeats;

    @Column(name = "reward_on_referral_VC")
    private String rewardonReferralVC;

    @Column(name = "reward_on_referral_value")
    private String rewardonReferralValue;

    @Column(name = "referral_Id")
    private long referralid;

    @Column(name = "milestone_level")
    private long milestoneLevel;

}
