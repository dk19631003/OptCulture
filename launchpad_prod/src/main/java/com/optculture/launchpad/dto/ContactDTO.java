package com.optculture.launchpad.dto;

import java.time.LocalDateTime;
import java.util.Date;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactDTO {

	
	private String firstName;
	private String lastName;
    private String emailId;
    private String mobilePhone;
    private String addressOne;// street
    private String addressTwo;
    private String city;
    private String state;
    private String country;
    private LocalDateTime birthDay;
    private LocalDateTime anniversary;
    private String createdDate;
    private String gender;
    private String pin;
    
    
    
    // need to process 
    // GEN_contactHomestoreAddress

    //==========================================================
    			//common for all (Should in application.Properties)
    //==========================================================
  //Frequency Buyer program Status::|^GEN_FBPS / DEFAULT=Not Available^|\
  	//Loyalty Nudge::|^GEN_loyaltyNudge / DEFAULT=Not Available^|
  	//Unsubscribe Link::|^GEN_unsubscribeLink^|
  	//Sender Email Address::|^GEN_senderEmailAddress^|
  	//Web-Page Version Link::|^GEN_webPageVersionLink^|
  	//Share on Twitter::|^GEN_shareOnTwitter^|
  	//Share on Facebook::|^GEN_shareOnFacebook^|
  	//Forward To Friend::|^GEN_forwardToFriend^|
  	//Update Subscription Preference::|^GEN_updatePreferenceLink^|
  	//Confirm Subscription Link::|^GEN_confirmSubscriptionLink^|
  	



}
