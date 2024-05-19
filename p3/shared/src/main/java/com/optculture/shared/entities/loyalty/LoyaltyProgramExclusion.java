package com.optculture.shared.entities.loyalty;

import jakarta.persistence.*;
@Entity
@Table(name = "loyalty_program_exclusion")
public class LoyaltyProgramExclusion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "exclusion_id")
    private java.lang.Long exclusionId;

    @Column(name = "program_id")
    private java.lang.Long programId;

    @Column(name = "issuance_with_promo_flag")
    private char issuanceWithPromoFlag;

    @Column(name = "issuance_promo_id_str")
    private String issuancePromoIdStr;

    @Column(name = "redemption_with_promo_flag")
    private char redemptionWithPromoFlag;

    @Column(name = "redemption_promo_id_str")
    private String redemptionPromoIdStr;

    @Column(name = "store_number_str")
    private String storeNumberStr;

    @Column(name = "item_cat_str")
    private String itemCatStr;

    @Column(name = "dept_code_str")
    private String deptCodeStr;

    @Column(name = "class_str")
    private String classStr;

    @Column(name = "sub_class_str")
    private String subClassStr;

    @Column(name = "dcs_str")
    private String dcsStr;

    @Column(name = "vendor_str")
    private String vendorStr;

    @Column(name = "sku_num_str")
    private String skuNumStr;

    @Column(name = "date_str")
    private String dateStr;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "created_by")
    private String createdBy;

    @Column(name = "modified_date")
    private java.util.Calendar modifiedDate;

    @Column(name = "modified_by")
    private String modifiedBy;

    @Column(name = "str_redemp_chk")
    private java.lang.Boolean strRedempChk;

    @Column(name = "all_str_chk")
    private java.lang.Boolean allStrChk;

    @Column(name = "selected_store")
    private String selectedStoreStr;

    @Column(name = "exclu_redem_datestr")
    private String exclRedemDateStr;

}
