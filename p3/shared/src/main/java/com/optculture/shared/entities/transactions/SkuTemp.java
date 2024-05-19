package com.optculture.shared.entities.transactions;

import jakarta.persistence.*;
@Entity
@Table(name = "sku_temp")
public class SkuTemp {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "sku_temp_id")
    private java.lang.Long skuTempId;

    @Column(name = "sku_attribute")
    private String skuAttribute;

    @Column(name = "sku_value")
    private String skuValue;

    @Column(name = "program")
    private String program;

    @Column(name = "tier_num")
    private String tierNum;

    @Column(name = "CardSet_Num")
    private String cardSetNum;

    @Column(name = "user_id")
    private long userId;

    @Column(name = "coupon_id")
    private long couponId;

    @Column(name = "discount")
    private double discount;

    @Column(name = "discount_type")
    private String discountType;

    @Column(name = "total_purchase_amt")
    private long totPurchaseAmount;

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

    @Column(name = "eli_rule")
    private String eliRule;

}
