package com.optculture.shared.entities.org;

import jakarta.persistence.*;
@Entity
@Table(name = "products_used")
public class ProductsUsed {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private java.lang.Long productId;

    @Column(name = "number_of_contracted_emails")
    private java.lang.Long numberOfContractedEmails;

    @Column(name = "number_of_contracted_sms")
    private java.lang.Long numberOfContractedSms;

    @Column(name = "pos_system_version")
    private String POSSystemVersion;

    @Column(name = "opt_digital_reciept_version")
    private String optDigitalRecieptVersion;

    @Column(name = "opt_loyalty_version")
    private String optLoyaltyVersion;

    @Column(name = "opt_promo_version")
    private String optPromoVersion;

    @Column(name = "opt_intel_version")
    private String optIntelVersion;

    @Column(name = "opt_sync_version")
    private String optSyncVersion;

    @Column(name = "user_id")
    private java.lang.Long userId;

}
