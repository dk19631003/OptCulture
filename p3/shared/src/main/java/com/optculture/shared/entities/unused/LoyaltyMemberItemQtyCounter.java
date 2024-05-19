package com.optculture.shared.entities.unused;

import jakarta.persistence.*;
@Entity
@Table(name = "loyalty_membership_item_quantinty")
public class LoyaltyMemberItemQtyCounter {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private java.lang.Long id;

    @Column(name = "loyalty_id")
    private java.lang.Long loyaltyID;

    @Column(name = "sp_rule_id")
    private java.lang.Long SPRuleID;

    @Column(name = "item_str")
    private String itemStr;

    @Column(name = "used_id")
    private java.lang.Long userId;

    @Column(name = "org_id")
    private java.lang.Long orgId;

    @Column(name = "qty")
    private double qty;

}
