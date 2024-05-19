package com.optculture.shared.entities.promotion;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "referral_codes_issued")
public class ReferralcodesIssued {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "referral_code_id")
    private java.lang.Long referralCodeId;

    @Column(name = "code")
    private String Refcode;

    @Column(name = "name", length = 60)
    private String RefprogramName;

    @Column(name = "referred_cid")
    private java.lang.Long referredCid;

    @Column(name = "status")
    private String status;

    @Column(name = "issued_date")
    private java.util.Calendar issuedDate;

    @Column(name = "created_by")
    private Long userId;

    @Column(name = "org_id")
    private Long orgId;

    @Column(name = "campaign_type")
    private String campaignType;

    @Column(name = "sent_to")
    private String sentTo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private com.optculture.shared.entities.promotion.Coupons couponId;

}
