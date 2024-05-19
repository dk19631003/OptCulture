package com.optculture.shared.entities.loyalty;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "loyalty_settings")
public class LoyaltySetting {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "lty_setting_id")
    private Long ltySettingId;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_org_id")
    private Long userOrgId;

    @Column(name = "email", length = 60)
    private String email;

    @Column(name = "mobile", length = 20)
    private String mobile;

    @Column(name = "url_str")
    private String urlStr;

    @Column(name = "color_code")
    private String colorCode;

    @Column(name = "path")
    private String companyLogo;

    @Column(name = "is_active")
    private String isActive;

    @Column(name = "banner_image_path")
    private String bannerPath;

    @Column(name = "homepage_image_path")
    private String homePageImagePath;

    @Column(name = "homepage_color_code")
    private String homePageColorCode;

    @Column(name = "bonus_card_color_code")
    private String balanceCardColorCode;

    @Column(name = "bonus_card_text_color_code")
    private String balanceCardTextColorCode;

    @Column(name = "loyalty_type")
    private String loyaltyType;

    @Column(name = "org_owner")
    private Long orgOwner;

    @Column(name = "font_name")
    private String fontName;

    @Column(name = "font_url")
    private String fontURL;

    @Column(name = "banner_name")
    private String bannerName;

    @Column(name = "tab_name")
    private String tabName;

    @Column(name = "tab_image_path")
    private String tabImagePath;

    @Column(name = "include_firstname")
    private Boolean includeFirstname;

    @Column(name = "include_lastname")
    private Boolean includeLastname;

    @Column(name = "include_mobilenumber")
    private Boolean includeMobilenumber;

    @Column(name = "include_emailaddress")
    private Boolean includeEmailaddress;

    @Column(name = "include_street")
    private Boolean includeStreet;

    @Column(name = "include_city")
    private Boolean includeCity;

    @Column(name = "include_state")
    private Boolean includeState;

    @Column(name = "include_postalcode")
    private Boolean includePostalCode;

    @Column(name = "include_country")
    private Boolean includeCountry;

    @Column(name = "include_birthday")
    private Boolean includeBirthday;

    @Column(name = "include_anniversary")
    private Boolean includeAnniversary;

    @Column(name = "include_gender")
    private Boolean includeGender;
    
    @Column(name = "smart_ereceipt_json_config")
    private String smartEreceiptJsonConfig;

    @Column(name = "referral_image")
    private String referralImage;

}
