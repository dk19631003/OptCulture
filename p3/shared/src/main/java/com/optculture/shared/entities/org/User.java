package com.optculture.shared.entities.org;

import java.util.Calendar;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.optculture.shared.entities.config.Vmta;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "mqs_id", length = 20)
    private String mqsId;

    @Column(name = "username", length = 400)
    private String userName;

    @Column(name = "password", length = 256)
    private String password;

    @Column(name = "emailid", length = 60)
    private String emailId;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 75)
    private String lastName;

    @Column(name = "company_name", length = 100)
    private String companyName;

    @Column(name = "created_date")
    private java.util.Calendar createdDate;

    @Column(name = "address_one", length = 55)
    private String addressOne;

    @Column(name = "address_two", length = 55)
    private String addressTwo;

    @Column(name = "city", length = 55)
    private String city;

    @Column(name = "state", length = 45)
    private String state;

    @Column(name = "country", length = 45)
    private String country;

    @Column(name = "pin_code", length = 20)
    private String pinCode;

    @Column(name = "phone", length = 30)
    private String phone;

    @Column(name = "enabled")
    private Boolean enabled;

    @Column(name = "email_count")
    private Integer emailCount;

    @Column(name = "used_email_count")
    private Integer usedEmailCount;
    
    @Column(name = "package_expiry_date")
    private Calendar packageExpiryDate;
    
    @Column(name = "user_activity_settings")
    private String userActivitySettings;
    
    @Column(name = "package_start_date")
    private Calendar packageStartDate;

    @Column(name = "footer_editor")
    private Byte footerEditor;

    @Column(name = "sms_count")
    private Integer smsCount;
    
    @Column(name = "used_sms_count")
    private Integer usedSmsCount;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_organization")
    private UserOrganization userOrganization;
    
    @Column(name = "account_type", length = 100)
    private String accountType;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_user_id")
    private User parentUser;
    
    @Column(name = "token", length = 100)
    private String token;
    
    @Column(name = "digital_receipt_extraction")
    private Boolean digitalReceiptExtraction;
    
    @Column(name = "cim_profile_id", length = 20)
    private String CIMProfileId;
    
    @Column(name = "client_time_zone")
    private String clientTimeZone;
    
    @Column(name = "user_sms_tool", length = 100)
    private String userSmsTool;
    
    @Column(name = "country_carrier")
    private Short countryCarrier;
    
    @Column(name = "msg_chk_type", length = 100)
    private String MsgChkType;
    
    @Column(name = "subscription_enabled")
    private Boolean subscriptionEnable;
    
    @Column(name = "country_type", length = 100)
    private String countryType;
    
    @Column(name = "enable_Sms")
    private Boolean enableSms;

    @Column(name = "consider_Sms_settings")
    private Boolean considerSmsSettings;

    @Column(name = "last_logged_in_time")
    private Calendar lastLoggedInTime;
    
    @Column(name = "optin_medium")
    private Byte optInMedium;
    
    @Column(name = "weekly_report_email_id")
    private String weeklyReportEmailId;

    @Column(name = "camp_exp_email_id", length = 150)
    private String campExpEmailId;

    @Column(name = "weekly_report_time")
    private java.util.Date weeklyReportTime;

    @Column(name = "weekly_report_day")
    private Integer weeklyReportDay;
    
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vmta")
    private Vmta vmta;
    
    @Column(name = "loyalty_service_type")
    private String loyaltyServicetype;
    
    @Column(name = "enable_unsub_link")
    private Boolean enableUnsublink;

    @Column(name = "unsub_url")
    private String unsuburl;

    @Column(name = "weekly_report_type_email")
    private Boolean weeklyReportTypeEmail;

    @Column(name = "weekly_report_type_sms")
    private Boolean weeklyReportTypeSms;

    @Column(name = "POS_version")
    private String POSVersion;
    
    @Column(name = "mandatory_update_pwd_on")
    private Calendar mandatoryUpdatePwdOn;

    @Column(name = "optin_route", length = 100)
    private String optinRoute;
    
    @Column(name = "zone_wise")
    private Boolean zoneWise;

    @Column(name = "optin_mobile_by_default")
    private Boolean optinMobileByDefault;
    
    @Column(name = "enable_loyalty_extraction")
    private Boolean enableLoyaltyExtraction;

    @Column(name = "enroll_from_DR")
    private Boolean enrollFromDR;

    @Column(name = "issuance_from_DR")
    private Boolean issuanceFromDR;

    @Column(name = "return_from_DR")
    private Boolean returnFromDR;

    @Column(name = "perform_redeemed_amount_reversal")
    private Boolean performRedeemedAmountReversal;

    @Column(name = "redemption_from_DR")
    private Boolean redemptionFromDR;

    @Column(name = "redeem_tender", length = 100)
    private String redeemTender;
    
    @Column(name = "new_plugin")
    private Boolean newPlugin;

    @Column(name = "is_specific_dir")
    private Boolean isSpecificDir;
    
    @Column(name = "enable_promo_redemption")
    private Boolean enablePromoRedemption;
    
    @Column(name = "validate_items_in_return_trx")
    private Boolean validateItemsInReturnTrx;

    @Column(name = "ignore_points_redemption")
    private Boolean ignorePointsRedemption;

    @Column(name = "contract_stores", length = 50)
    private String contractStores;

    @Column(name = "contract_contacts", length = 50)
    private String contractContacts;

    @Column(name = "contract_sms_added", length = 50)
    private String contractSmsAdded;

    @Column(name = "contract_e_receipt_restricted", length = 50)
    private String contractEReceiptsRestricted;
    
    @Column(name = "item_note_used", length = 100)
    private String itemNoteUsed;

    @Column(name = "receipt_note_used", length = 100)
    private String receiptNoteUsed;

    @Column(name = "item_info", length = 100)
    private String itemInfo;

    @Column(name = "card_info", length = 100)
    private String cardInfo;

    @Column(name = "redeem_tender_displabel")
    private String redeemTenderDispLabel;

    @Column(name = "receipt_on_sms")
    private Boolean receiptOnSms;
    
    @Column(name = "non_inventory_item", length = 100)
    private String nonInventoryItem;
    
    @Column(name = "ignore_trx_upon_extraction")
    private Boolean ignoretrxUpOnExtraction;
    
    @Column(name = "DR_SMS_content", length = 1000)
    private String DRSmsContent;

    @Column(name = "DR_SMS_tempregID", length = 50)
    private String DRSmsTempRegID;

    @Column(name = "DRSMSSenderID", length = 50)
    private String DRSmsSenderID;
    
    @Column(name = "allow_both_discounts")
    private Boolean allowBothDiscounts;
    
    @Column(name = "redemption_as_discount")
    private Boolean redemptionAsDiscount;

    @Column(name = "receipt_on_WA")
    private Boolean receiptOnWA;

    @Column(name = "show_only_highest_disc_receiptDC")
    private Boolean showOnlyHighestDiscReceiptDC;
    
    @Column(name = "send_expiry_info")
    private Boolean sendExpiryInfo;
    
    @Column(name = "selected_expiry_info_type")
    private String selectedExpiryInfoType;

    @Column(name = "WA_API_key", length = 50)
    private String WAAPIKey;

    @Column(name = "WA_API_endpoint_URL", length = 400)
    private String WAAPIEndPointURL;

    @Column(name = "WA_template_id", length = 50)
    private String WATemplateID;

    @Column(name = "WA_user_id", length = 50)
    private String WAUserID;

    @Column(name = "WA_JSON_Template", length = 2000)
    private String WAJSONTemplate;
    
    @Column(name = "exclude_discounted_item")
    private Boolean excludeDiscountedItem;

    @Column(name = "show_only_highest_ltyDC")
    private Boolean showOnlyHighestLtyDC;

    @Column(name = "ignore_issuance_on_redemption")
    private Boolean ignoreissuanceOnRedemption;
    
    @Column(name = "enable_NPS")
    private Boolean enableNPS;

    @Column(name = "NPS_product_key", length = 500)
    private String NPSProductKey;

    @Column(name = "NPS_endpoint_URL", length = 500)
    private String NPSEndPointURL;

    @Column(name = "NPS_JSON_template", length = 500)
    private String NPSJSONTemplate;

    @Column(name = "NPS_cookie", length = 500)
    private String NPSCookie;

    @Column(name = "co_on_WA", columnDefinition = "bit")
    private Boolean coOnWA;

    @Column(name = "confirmorder_JSON_Template", length = 1000)
    private String confirmOrderJSONTemplate;

    @Column(name = "co_cookie", length = 1000)
    private String coCookie;

    @Column(name = "exclude_gift_redemption")
    private Boolean excludeGiftRedemption;
    
    @Column(name = "enable_smart_ereceipt")
    private Boolean enableSmartEReceipt;

//	@Column(name = "ereceipt_configured")
//	private String ereceiptConfigured;

//    @Column(name = "enable_clickhouse_DB_Flag")
//    private Boolean enableClickHouseDBFlag;


@ManyToMany
@JoinTable(
    name = "users_roles",
    joinColumns = @JoinColumn(name = "user_id"),
    inverseJoinColumns = @JoinColumn(name = "role_id")
)
private Set<com.optculture.shared.entities.config.SecRoles> roles;





    /*
     * TODO: Convert Set
     * <set name="userDomains" table="users_domains">
     * <key column="user_id" />
     * <many-to-many class="org.mq.marketer.campaign.beans.UsersDomains"
     * column="domain_id" />
     * </set>
     */
    /*
     * TODO: Convert Set
     * <set name="roles" table="users_roles">
     * <key column="user_id" />
     * <many-to-many class="org.mq.marketer.campaign.beans.SecRoles"
     * column="role_id" />
     * </set>
     */
}
