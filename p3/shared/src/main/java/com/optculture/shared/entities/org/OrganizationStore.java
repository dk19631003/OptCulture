package com.optculture.shared.entities.org;

import java.util.Calendar;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "org_stores")
public class OrganizationStore {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "store_id")
    private Long storeId;

    @Column(name = "home_store_id", length = 20)
    private String homeStoreId;

    @Column(name = "store_name")
    private String storeName;

    @Column(name = "store_manager_name")
    private String storeManagerName;

    @Column(name = "created_date")
    private Calendar createdDate;

    @Column(name = "modified_date")
    private Calendar modifiedDate;

    @Column(name = "email_id")
    private String emailId;

    @Column(name = "website")
    private String website;

    @Column(name = "from_email_id")
    private String fromEmailId;

    @Column(name = "reply_to_email_id")
    private String replyToEmailId;

    @Column(name = "from_name")
    private String fromName;

    @Column(name = "address_flag")
    private Boolean addressFlag;

    @Column(name = "address", length = 1000)
    private String addressStr;

    @Column(name = "store_brand")
    private String storeBrand;

    @Column(name = "locality")
    private String locality;

    @Column(name = "city")
    private String city;

    @Column(name = "state")
    private String state;

    @Column(name = "country")
    private String country;

    @Column(name = "zip_code")
    private String zipCode;

    @Column(name = "mobile_no")
    private String mobileNo;

    @Column(name = "subsidary_id")
    private String subsidiaryId;

    @Column(name = "subsidiary_name")
    private String subsidiaryName;

    @Column(name = "domain_id")
    private Long domainId;

    @Column(name = "erp_store_id")
    private String erpStoreId;

    @Column(name = "time_zone")
    private String timeZone;

    @Column(name = "latitude")
    private String latitude;

    @Column(name = "longitude")
    private String longitude;

    @Column(name = "store_Image_Path")
    private String storeImagePath;

    @Column(name = "brand_Image_Path")
    private String brandImagePath;

    @Column(name = "description")
    private String description;

    @Column(name = "google_map_link")
    private String googleMapLink;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_id")
    private UserOrganization userOrganization;

}
