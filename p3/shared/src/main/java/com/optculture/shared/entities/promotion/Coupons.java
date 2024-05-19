package com.optculture.shared.entities.promotion;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Data
@Table(name = "coupons")
public class Coupons {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_id")
    private java.lang.Long couponId;

    @Column(name = "coupon_name")
    private String couponName;

    @Column(name = "coupon_description")
    private String couponDescription;

    @Column(name = "coupon_code")
    private String couponCode;

    @Column(name = "coupon_status")
    private String status;

    @Column(name = "coupon_gen_type")
    private String couponGeneratedType;

    @Column(name = "ctcoupon_code")
    private String CTCouponCode;

    @Column(name = "ctcoupon_value")
    private String CTCouponValue;

    @Column(name = "purchase_qty")
    private Double purchaseQty;

    @Column(name = "discount_type")
    private String discountType;

    @Column(name = "discount_criteria")
    private String discountCriteria;

    @Column(name = "total_qty")
    private Long totalQty;

    @Column(name = "generated_qty")
    private Long generatedQty;

    @Column(name = "org_id")
    private Long orgId;

    @Column(name = "special_rewad_id")
    private Long specialRewadId;

    @Column(name = "user_created_coupon_date")
    private LocalDateTime userCreatedDate;

    @Column(name = "last_modified_user")
    private String lastModifiedUser;

    @Column(name = "user_last_modified_date")
    private LocalDateTime userLastModifiedDate;

    @Column(name = "coupon_created_date")
    private LocalDateTime couponCreatedDate;

    @Column(name = "coupon_expiry_date")
    private LocalDateTime couponExpiryDate;

    @Column(name = "auto_increment_check")
    private Boolean autoIncrCheck;

    @Column(name = "issued")
    private Long issued;

    @Column(name = "redeemed")
    private Long redeemed;

    @Column(name = "available")
    private Long available;

    @Column(name = "tot_discount")
    private Double totDiscount;

    @Column(name = "tot_revenue")
    private Double totRevenue;

    @Column(name = "redeemd_count")
    private Long redeemdCount;

    @Column(name = "redemed_auto_check")
    private Boolean redemedAutoChk;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "apply_default")
    private Boolean applyDefault;

    @Column(name = "use_as_referral_code")
    private Boolean useasReferralCode;

    @Column(name = "loyalty_points")
    private Byte loyaltyPoints;

    @Column(name = "required_loylty_points")
    private Integer requiredLoyltyPoits;

    @Column(name = "used_loyalty_points")
    private Double usedLoyaltyPoints;

    @Column(name = "enable_barcode")
    private Boolean enableBarcode;

    @Column(name = "barcode_type")
    private String barcodeType;

    @Column(name = "barcode_width")
    private Long barcodeWidth;

    @Column(name = "barcode_height")
    private Long barcodeHeight;

    @Column(name = "single_promo_cont_limit_redem_chk")
    private Boolean singPromoContUnlimitedRedmptChk;

    @Column(name = "single_promo_cont_redeem_limit")
    private Long singPromoContRedmptLimit;

    @Column(name = "all_store_chk")
    private Boolean allStoreChk;

    @Column(name = "selected_stores")
    private String selectedStores;

    @Column(name = "expiry_type")
    private String expiryType;

    @Column(name = "expiry_details")
    private String expiryDetails;

    @Column(name = "stackable")
    private Boolean stackable;

    @Column(name = "exclude_items")
    private Boolean excludeItems;

    @Column(name = "accumulate_with_Promo")
    private Boolean accumulateOtherPromotion;

    @Column(name = "no_of_eligile_items")
    private String noOfEligibleItems;

    @Column(name = "value_code")
    private String valueCode;

    @Column(name = "enable_Offer")
    private Boolean enableOffer;

    @Column(name = "banner_Image")
    private String bannerImage;

    @Column(name = "offer_Heading")
    private String offerHeading;

    @Column(name = "offer_Description")
    private String offerDescription;

    @Column(name = "banner_Url_Redirect")
    private String bannerUrlRedirect;

    @Column(name = "mapped_on_zone")
    private Boolean mappedOnZone;

    @Column(name = "highlighted_offer")
    private Boolean highlightedOffer;

    @Column(name = "combine_item_attributes")
    private Boolean combineItemAttributes;

    @Column(name = "otp_authen_check")
    private Boolean otpAuthenCheck;

    @Column(name = "coupcode_gen_type")
    private String coupCodeGenType;

    @Column(name = "multiplier_value")
    private Double multiplierValue;

/*
TODO: Convert Set
<set name="brand" table="brand_coupons" lazy="false" cascade="save-update" fetch="select">
			<key column="coupon_id" />
			<many-to-many class="org.mq.marketer.campaign.beans.OrganizationZone" column="zone_id" />
		</set>
*/
}
