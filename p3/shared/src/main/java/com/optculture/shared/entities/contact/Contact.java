package com.optculture.shared.entities.contact;

import java.time.LocalDateTime;
import java.util.Calendar;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "contacts")
public class Contact {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cid")
    private Long contactId;

    @Column(name = "email_id", length = 60)
    private String emailId;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(name = "created_date")
    private LocalDateTime createdDate;

    @Column(name = "purged")
    private Boolean purged;

    @Column(name = "email_status", length = 255)
    private String emailStatus;

    @Column(name = "last_status_change")
    private LocalDateTime lastStatusChange;

    @Column(name = "last_mail_date")
    private LocalDateTime lastMailDate;

    @Column(name = "last_sms_date")
    private LocalDateTime lastSmsDate;

    @Column(name = "address_one", length = 100)
    private String addressOne;

    @Column(name = "address_two", length = 100)
    private String addressTwo;

    @Column(name = "city", length = 50)
    private String city;

    @Column(name = "state", length = 50)
    private String state;

    @Column(name = "country", length = 50)
    private String country;

    @Column(name = "pin", length = 10)
    private Integer pinCode;

    @Column(name = "phone", length = 20)
    private Long phone;

    @Column(name = "optin")
    private Byte optin;

    @Column(name = "subscription_type", length = 30)
    private String subscriptionType;

    @Column(name = "external_id", length = 45)
    private String externalId;

    @Column(name = "optin_medium", length = 30)
    private String optinMedium;

    @Column(name = "mobile_opt_in")
    private Boolean mobileOptin;

    @Column(name = "mark")
    private Boolean mark;

    @Column(name = "gender", length = 30)
    private String gender;

    @Column(name = "birth_day")
    private LocalDateTime birthDay;

    @Column(name = "anniversary_day")
    private LocalDateTime anniversary;

    @Column(name = "home_store", length = 30)
    private String homeStore;

    @Column(name = "subsidiary_number")
    private String subsidiaryNumber;

    @Column(name = "udf1", length = 30)
    private String udf1;

    @Column(name = "udf2", length = 30)
    private String udf2;

    @Column(name = "udf3", length = 30)
    private String udf3;

    @Column(name = "udf4", length = 30)
    private String udf4;

    @Column(name = "udf5", length = 30)
    private String udf5;

    @Column(name = "udf6", length = 30)
    private String udf6;

    @Column(name = "udf7", length = 30)
    private String udf7;

    @Column(name = "udf8", length = 30)
    private String udf8;

    @Column(name = "udf9", length = 30)
    private String udf9;

    @Column(name = "udf10", length = 30)
    private String udf10;

    @Column(name = "udf11", length = 30)
    private String udf11;

    @Column(name = "udf12", length = 30)
    private String udf12;

    @Column(name = "udf13", length = 30)
    private String udf13;

    @Column(name = "udf14", length = 30)
    private String udf14;

    @Column(name = "udf15", length = 30)
    private String udf15;

    @Column(name = "opted_into")
    private Byte optedInto;

    @Column(name = "optin_per_type")
    private Byte optinPerType;

    @Column(name = "loyalty_customer")
    private Byte loyaltyCustomer;

    @Column(name = "hp_id")
    private Long hpId;

    @Column(name = "mobile_status", length = 255)
    private String mobileStatus;

    @Column(name = "zip")
    private String zip;

    @Column(name = "mobile_phone")
    private String mobilePhone;

    @Column(name = "home_phone")
    private String homePhone;

    @Column(name = "mlbits")
    private Long mlBits;

    @Column(name = "categories", length = 1000)
    private String categories;

    @Column(name = "last_mail_span")
    private Integer lastMailSpan;

    @Column(name = "last_sms_span")
    private Integer lastSmsSpan;

    @Column(name = "push_notification")
    private Boolean pushNotification;

    @Column(name = "language")
    private String language;

    @Column(name = "instance_id")
    private String instanceId;

    @Column(name = "device_Type", length = 50)
    private String deviceType;

    @Column(name = "modified_date")
    private LocalDateTime modifiedDate;

    @Column(name = "mobile_optin_source", length = 20)
    private String mobileOptinSource;

    @Column(name = "mobile_optin_date")
    private LocalDateTime mobileOptinDate;

    @Column(name = "user_id")
    private Long userId;
    
        

}
