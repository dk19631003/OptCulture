package com.optculture.shared.entities.promotion;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
@Data
@Entity
@Table(name = "coupon_codes")
public class CouponCodes  implements Comparable<CouponCodes>  {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_code_id")
    private java.lang.Long ccId;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "sentid")
    private Long sentId;

    @Column(name = "campaign_type")
    private String campaignType;

    @Column(name = "orgId")
    private Long orgId;

    @Column(name = "status")
    private String status;

    @Column(name = "issued_on")
    private LocalDateTime issuedOn;

    @Column(name = "redeemed_on")
    private LocalDateTime redeemedOn;

    @Column(name = "tot_discount")
    private Double totDiscount;

    @Column(name = "tot_revenue")
    private Double totRevenue;

    @Column(name = "issued_to")
    private String issuedTo;

    @Column(name = "membership")
    private String membership;

    @Column(name = "campaign_name")
    private String campaignName;

    @Column(name = "used_loyalty_points")
    private Double usedLoyaltyPoints;

    @Column(name = "value_code")
    private String valueCode;

    @Column(name = "contact_id")
    private Long contactId;

    @Column(name = "doc_sid")
    private String docSid;

    @Column(name = "subsidiary_number")
    private String subsidiaryNumber;

    @Column(name = "receipt_amount")
    private Double receiptAmount;

    @Column(name = "receipt_number")
    private String receiptNumber;

    @Column(name = "redeemed_to")
    private String redeemedTo;

    @Column(name = "redeem_cust_id")
    private String redeemCustId;

    @Column(name = "redeem_email_id")
    private String redeemEmailId;

    @Column(name = "redeem_phn_id")
    private String redeemPhnId;

    @Column(name = "store_number")
    private String storeNumber;

    @Column(name = "source_type", length = 50)
    private String sourceType;

    @Column(name = "expired_on")
    private LocalDateTime expiredOn;

    @Column(name = "item_info")
    private String itemInfo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private com.optculture.shared.entities.promotion.Coupons couponId;
    
    @Override
    public int compareTo(CouponCodes cc) {
		if(cc.getCouponCode()==null || this.couponCode==null) return 0;
		return cc.getCouponCode().compareTo(this.couponCode);	
	}

}
