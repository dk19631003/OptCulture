package com.optculture.shared.entities.promotion;

import jakarta.persistence.*;
@Entity
@Table(name = "coupon_discount_generation")
public class CouponDiscountGeneration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "coupon_dis_gen_Id")
    private java.lang.Long couponDisGenId;

    @Column(name = "discount")
    private double discount;

    @Column(name = "item_category")
    private String itemCategory;

    @Column(name = "total_purchase_amt")
    private long totPurchaseAmount;

    @Column(name = "store_number")
    private String storeNumber;

    @Column(name = "value")
    private String SkuValue;

    @Column(name = "attribute")
    private String SkuAttribute;

    @Column(name = "owner_id")
    private java.lang.Long ownerId;

    @Column(name = "max_discount")
    private double maxDiscount;

    @Column(name = "quantity")
    private String quantity;

    @Column(name = "limit_quantity")
    private String limitQuantity;

    @Column(name = "itemPrice")
    private double itemPrice;

    @Column(name = "itemPriceCriteria")
    private String itemPriceCriteria;

    @Column(name = "no_of_eligile_items")
    private String noOfEligibleItems;

    @Column(name = "shipping_fee")
    private String shippingFee;

    @Column(name = "program")
    private String Program;

    @Column(name = "tier_num")
    private String tierNum;

    @Column(name = "CardSet_Num")
    private String CardSetNum;

    @Column(name = "eli_rule")
    private String eliRule;

    @Column(name = "shipping_fee_type")
    private String shippingFeeType;

    @Column(name = "shipping_fee_free")
    private String shippingFeeFree;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "coupon_id")
    private com.optculture.shared.entities.promotion.Coupons coupons;

}
