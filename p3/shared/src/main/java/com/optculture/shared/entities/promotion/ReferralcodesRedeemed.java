package com.optculture.shared.entities.promotion;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
@Table(name = "referral_codes_redeemed")
public class ReferralcodesRedeemed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "referral_id")
    private java.lang.Long referralId;

    @Column(name = "name", length = 60)
    private String referredName;

    @Column(name = "refcode_status")
    private String refcodestatus;

    @Column(name = "referral_code_id")
    private java.lang.Long referralcodeid;

    @Column(name = "code")
    private String refcode;

    @Column(name = "doc_sid")
    private String docSID;

    @Column(name = "referred_cid")
    private java.lang.Long referredCid;

    @Column(name = "referee_cid")
    private java.lang.Long refereeCid;

    @Column(name = "created_by")
    private Long userId;

    @Column(name = "org_id")
    private Long orgId;

    @Column(name = "redeemed_date")
    private java.util.Calendar redeemedDate;

    @Column(name = "tot_revenue")
    private Double totRevenue;

}
