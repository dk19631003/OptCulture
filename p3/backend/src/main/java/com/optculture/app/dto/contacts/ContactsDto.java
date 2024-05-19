package com.optculture.app.dto.contacts;

import com.optculture.app.dto.coupons.CouponInventoryDto;
import com.optculture.app.dto.coupons.ReferralCodesDto;
import com.optculture.app.dto.ereceipt.Loyalty;
import com.optculture.app.dto.sales.OrderValueAggregation;
import com.optculture.shared.entities.contact.Contact;
import com.optculture.shared.entities.contact.ContactLoyalty;
import lombok.AccessLevel;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.List;

@RequiredArgsConstructor
@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ContactsDto {

    String lastName;
    LocalDateTime createdDate;
    String emailId;
    String firstName;
    String currentTierName;
    String cid;
    String membershipNumber;
    String membershipStatus;
    String mobilePhone;
    String gender;
    LocalDateTime birthDay;
    LocalDateTime anniversary;
    String addressOne;
    String addressTwo;
    String city;
    String state;
    String country;
    String zip;
    //cust fields
    String udf1;
    String udf2;
    String udf3;
    String udf4;
    String udf5;
    String udf6;
    String udf7;
    String udf8;
    String udf9;
    String udf10;
    String udf11;
    String udf12;
    String udf13;
    String udf14;
    String udf15;
    String loyaltyId;
    LocalDateTime lastInteraction;
    Integer lastNpsRating;
    //other info
    OrderValueAggregation OrdersInfo;
    Loyalty loyalty;
    ReferralCodesDto referralInfo;
    List<CouponInventoryDto> inventoryCoupons;
}
