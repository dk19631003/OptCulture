package org.mq.marketer.campaign.beans;
// Generated Nov 17, 2008 10:20:05 AM by Hibernate Tools 3.2.0.CR1


import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Contacts generated by hbm2java
 */
@SuppressWarnings({"unchecked","serial"})
public class Contacts  implements java.io.Serializable, Cloneable {


     private Long contactId;
     //private MailingList mailingList;
     
     //private Set<MailingList> mlSet = new HashSet<MailingList>(0);
     //private Set mlSet;
     private Users users;
     
     private String emailId;
     private String firstName;
     private String lastName;
     private Calendar createdDate;
     private Boolean purged = false;
     private String emailStatus;
     private Calendar lastStatusChange;
     private Calendar lastMailDate;
     private String addressOne;
     private String addressTwo;
     private String city;
     private String state;
     private String country;
     private int pin;
     private Long phone;
     private Byte optin;
     private String subscriptionType;
     private String mobileStatus;
     
     
     private String udf2;
     private String udf3;
     private String udf4;
     private String udf5;
     private String udf6;
     private String udf7;
     private String udf8;
     private String udf9;
     private String udf10;
     private String udf11;
     private String udf12;
     private String udf13;
     private String udf14;
     private String udf15;
     private Calendar modifiedDate;
	private String instanceId;
    private String deviceType;
	private String gender;
	private Calendar birthDay;
	private Calendar anniversary;
	private Byte optedInto;
	private Byte optinPerType;
	private String optinMedium;
	private String activityDate;
	private String externalId;
	private String homeStore;
	private String subsidiaryNumber;

	


	public String getSubsidiaryNumber() {
		return subsidiaryNumber;
	}

	public void setSubsidiaryNumber(String subsidiaryNumber) {
		this.subsidiaryNumber = subsidiaryNumber;
	}


	private Long hpId;
     private Byte loyaltyCustomer;
     
    private String zip;
    private String mobilePhone;
    private String homePhone; 

	private Calendar lastSMSDate;
	private boolean mobileOptin;
	private boolean mark;
	
	public boolean isMark() {
		return mark;
	}

	public void setMark(boolean mark) {
		this.mark = mark;
	}


	private Long mlBits;
	
	
	private String categories;
	private int lastMailSpan;
	private int lastSmsSpan;
	private String language;
	private Boolean pushNotification=true;	
	
	private Calendar mobileOptinDate;
	private String mobileOptinSource;
	
	//dummy fields-- dont require a column in DB
		private String cardNumber;
		private String cardPin;
		private Double points;
		private Double rewardBalance;
		private Double giftBalance;
		private Double holdPoints;
		private Double holdCurrency;
		
		public String getCardNumber() {
			return cardNumber;
		}

		public void setCardNumber(String cardNumber) {
			this.cardNumber = cardNumber;
		}

		public String getCardPin() {
			return cardPin;
		}

		public void setCardPin(String cardPin) {
			this.cardPin = cardPin;
		}

		public Double getPoints() {
			return points;
		}

		public void setPoints(Double points) {
			this.points = points;
		}

		public Double getRewardBalance() {
			return rewardBalance;
		}

		public void setRewardBalance(Double rewardBalance) {
			this.rewardBalance = rewardBalance;
		}

		public Double getGiftBalance() {
			return giftBalance;
		}

		public void setGiftBalance(Double giftBalance) {
			this.giftBalance = giftBalance;
		}

		public Double getHoldPoints() {
			return holdPoints;
		}

		public void setHoldPoints(Double holdPoints) {
			this.holdPoints = holdPoints;
		}

		public Double getHoldCurrency() {
			return holdCurrency;
		}

		public void setHoldCurrency(Double holdCurrency) {
			this.holdCurrency = holdCurrency;
		}


	
	public Calendar getMobileOptinDate() {
		return mobileOptinDate;
	}

	public void setMobileOptinDate(Calendar mobileOptinDate) {
		this.mobileOptinDate = mobileOptinDate;
	}

	public String getMobileOptinSource() {
		return mobileOptinSource;
	}

	public void setMobileOptinSource(String mobileOptinSource) {
		this.mobileOptinSource = mobileOptinSource;
	}



	public void setUdf1(String udf1) {
		this.udf1 = udf1;
	}

	public void setUdf2(String udf2) {
		this.udf2 = udf2;
	}

	public void setUdf3(String udf3) {
		this.udf3 = udf3;
	}

	public void setUdf4(String udf4) {
		this.udf4 = udf4;
	}

	public void setUdf5(String udf5) {
		this.udf5 = udf5;
	}

	public void setUdf6(String udf6) {
		this.udf6 = udf6;
	}

	public void setUdf7(String udf7) {
		this.udf7 = udf7;
	}

	public void setUdf8(String udf8) {
		this.udf8 = udf8;
	}

	public void setUdf9(String udf9) {
		this.udf9 = udf9;
	}

	public void setUdf10(String udf10) {
		this.udf10 = udf10;
	}

	public void setUdf11(String udf11) {
		this.udf11 = udf11;
	}

	public void setUdf12(String udf12) {
		this.udf12 = udf12;
	}

	public void setUdf13(String udf13) {
		this.udf13 = udf13;
	}

	public void setUdf14(String udf14) {
		this.udf14 = udf14;
	}

	public void setUdf15(String udf15) {
		this.udf15 = udf15;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}


	private String udf1;
	     public String getUdf1() {
		return udf1;
	}
	
	public String getUdf2() {
		return udf2;
	}
	
	public String getUdf3() {
		return udf3;
	}
	
	public String getUdf4() {
		return udf4;
	}
	
	public String getUdf5() {
		return udf5;
	}
	
	public String getUdf6() {
		return udf6;
	}
	
	public String getUdf7() {
		return udf7;
	}
	
	public String getUdf8() {
		return udf8;
	}
	
	public String getUdf9() {
		return udf9;
	}
	
	public String getUdf10() {
		return udf10;
	}
	
	public String getUdf11() {
		return udf11;
	}
	
	public String getUdf12() {
		return udf12;
	}
	
	public String getUdf13() {
		return udf13;
	}
	
	public String getUdf14() {
		return udf14;
	}
	
	public String getUdf15() {
		return udf15;
	}
	
	public String getGender() {
		return gender;
	}


	

     public Calendar getBirthDay() {
    	 return birthDay;
     }

	public void setBirthDay(Calendar birthDay) {
		this.birthDay = birthDay;
	}
	
	public Calendar getAnniversary() {
		return anniversary;
	}
	
	public void setAnniversary(Calendar anniversary) {
		this.anniversary = anniversary;
	}
	
	public Byte getOptedInto() {
		return optedInto;
	}

	public void setOptedInto(Byte optedInto) {
		this.optedInto = optedInto;
	}
	
	public Byte getOptinPerType() {
		return optinPerType;
	}
	
	public void setOptinPerType(Byte optinPerType) {
		this.optinPerType = optinPerType;
	}


	 
     


	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	
	

	public String getOptinMedium() {
		return optinMedium;
	}

	public void setOptinMedium(String optinMedium) {
		this.optinMedium = optinMedium;
	}

	public Contacts() {
    }
	public Contacts(MailingList mailingList) {
		//this.mlSet.add(mailingList);
		this.mlBits = mailingList.getMlBit();
    }
    
    
/*    public Contacts(Contacts contact) {
        this.mailingList = contact.getMailingList();
        this.emailId = contact.getEmailId();
        this.firstName = contact.getFirstName();
        this.lastName = contact.getLastName();
        this.createdDate = new Date();
        this.optinStatus = contact.getOptinStatus();
        this.emailStatus = contact.getEmailStatus();
        this.addressOne = contact.getAddressOne();
        this.addressTwo = contact.getAddressTwo();
        this.city = contact.getCity();
        this.state = contact.getState();
        this.country = contact.getCountry();
        this.pin = contact.getPin();
        this.phone = contact.getPhone();
        this.optin = contact.getOptin();
        this.subscriptionType = contact.getSubscriptionType();
    }*/
	
    public Contacts(MailingList mailingList, String emailId, String emailStatus) {
        //this.mailingList = mailingList;
    	//this.mlSet.add(mailingList);
    	this.mlBits = mailingList.getMlBit();
        this.emailId = emailId;
        this.emailStatus = emailStatus;
    }
    
	public Contacts(MailingList mailingList, Calendar createdDate, String emailStatus) {
       //this.mailingList = mailingList;
	   //this.mlSet.add(mailingList);
		this.mlBits = mailingList.getMlBit();
       this.createdDate = createdDate;
       this.emailStatus = emailStatus;
    }

	/*public Contacts(MailingList mailingList, String emailId, String firstName, String lastName, 
			Calendar createdDate, Boolean purged, String emailStatus, Calendar lastStatusChange) {
       this.mailingList = mailingList;
       this.emailId = emailId;
       this.firstName = firstName;
       this.lastName = lastName;
       this.createdDate = createdDate;
       this.purged = purged;
       this.emailStatus = emailStatus;
       this.lastStatusChange = lastStatusChange;
    }*/

   /* public Contacts(MailingList mailingList, String emailId, String firstName, String lastName, 
    		Calendar createdDate, Boolean purged, String emailStatus, Calendar lastStatusChange, 
    		Calendar lastMailDate, String addressOne, String addressTwo, String city, String state,
    		String country, int pin, Long phone,byte optin,String subscriptionType) {
    	
       this.mailingList = mailingList;
       this.emailId = emailId;
       this.firstName = firstName;
       this.lastName = lastName;
       this.createdDate = createdDate;
       this.purged = purged;
       this.emailStatus = emailStatus;
       this.lastStatusChange = lastStatusChange;
       this.lastMailDate = lastMailDate;
       this.addressOne = addressOne;
       this.addressTwo = addressTwo;
       this.city = city;
       this.state = state;
       this.country = country;
       this.pin = pin;
       this.phone = phone;
       this.optin = optin;
       this.subscriptionType = subscriptionType;
    }*/
    
  /*  public Contacts(MailingList mailingList, String emailId, String firstName,
    		String lastName, Calendar createdDate, Boolean purged, String emailStatus,
    		String addressOne, String addressTwo, String city, String state, String country,
    		int pin, Long phone) {
    	
       this.mailingList = mailingList;
       this.emailId = emailId;
       this.firstName = firstName;
       this.lastName = lastName;
       this.createdDate = createdDate;
       this.purged = purged;
       this.emailStatus = emailStatus;
       this.addressOne = addressOne;
       this.addressTwo = addressTwo;
       this.city = city;
       this.state = state;
       this.country = country;
       this.pin = pin;
       this.phone = phone;
    }*/
   
  /*  public Contacts(MailingList mailingList, String emailId, String firstName, String lastName, 
    		Calendar createdDate, Boolean purged, String emailStatus, Calendar lastStatusChange,
    		String addressOne, String addressTwo, String city, String state, String country, int pin,
    		Long phone) {
    	
       this.mailingList = mailingList;
       this.emailId = emailId;
       this.firstName = firstName;
       this.lastName = lastName;
       this.createdDate = createdDate;
       this.purged = purged;
       this.emailStatus = emailStatus;
       this.lastStatusChange = lastStatusChange;
       this.addressOne = addressOne;
       this.addressTwo = addressTwo;
       this.city = city;
       this.state = state;
       this.country = country;
       this.pin = pin;
       this.phone = phone;
    }*/
   
    public Long getContactId() {
        return this.contactId;
    }
    
    public void setContactId(Long contactId) {
        this.contactId = contactId;
    }
    
   /* public MailingList getMailingList() {
        return this.mailingList;
    }*/
   /* public MailingList getMailingListByType(String listType) {
        
    	if(this.getContactBit() == null) return null;
    	
    	Iterator<MailingList> listIt = this.mlSet.iterator();
    	MailingList tempList = null;
    	while(listIt.hasNext()){
    		tempList = listIt.next();
    		if(tempList.getListType().equals(listType)) return tempList;
    	}
    	return null;
    }//getMailingListByType
*/    
    /*public void setMailingList(MailingList mailingList) {
        this.mailingList = mailingList;
        //this.mlSet.add(mailingList);
    }*/
    
   /* public Set<MailingList> getMlSet() {
		return mlSet;
	}

	public void setMlSet(Set<MailingList> mlSet) {
		this.mlSet = mlSet;
	}*/

	public Users getUsers() {
		return users;
	}

	public void setUsers(Users users) {
		this.users = users;
	}

	public String getEmailId() {
        return this.emailId;
    }
    
    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }
    public String getFirstName() {
        return this.firstName;
    }
    
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public String getLastName() {
        return this.lastName;
    }
    
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public Calendar getCreatedDate() {
        return this.createdDate;
    }
    
    public void setCreatedDate(Calendar createdDate) {
        this.createdDate = createdDate;
    }
    public Boolean getPurged() {
        return this.purged;
    }
    
    public void setPurged(Boolean purged) {
        this.purged = purged;
    }
    public String getEmailStatus() {
        return this.emailStatus;
    }
    
    public void setEmailStatus(String emailStatus) {
        this.emailStatus = emailStatus;
    }
    public Calendar getLastMailDate() {
        return this.lastMailDate;
    }
    
    public void setLastMailDate(Calendar lastMailDate) {
        this.lastMailDate = lastMailDate;
    }

    public Calendar getLastStatusChange() {
        return this.lastStatusChange;
    }
    
    public void setLastStatusChange(Calendar lastStatusChange) {
        this.lastStatusChange = lastStatusChange;
    }
    public String getAddressOne() {
        return this.addressOne;
    }
    
    public void setAddressOne(String addressOne) {
        this.addressOne = addressOne;
    }
    public String getAddressTwo() {
        return this.addressTwo;
    }
    
    public void setAddressTwo(String addressTwo) {
        this.addressTwo = addressTwo;
    }
    public String getCity() {
        return this.city;
    }
    
    public void setCity(String city) {
        this.city = city;
    }
    public String getState() {
        return this.state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    public String getCountry() {
        return this.country;
    }
    
    public void setCountry(String country) {
        this.country = country;
    }
    public int getPin() {
        return this.pin;
    }
    
    public void setPin(int pin) {
        this.pin = pin;
    }
    public Long getPhone() {
        return this.phone;
    }
    
    public void setPhone(Long phone) {
        this.phone = phone;
    }

	public Byte getOptin() {
		if(this.optin == null) 
			this.optin = Byte.valueOf((byte)(0));
		return optin;
	}

	public void setOptin(Byte optin) {
		this.optin = optin;
	}

	public String getSubscriptionType() {
		return subscriptionType;
	}

	public void setSubscriptionType(String subscriptionType) {
		this.subscriptionType = subscriptionType;
	}
	
	
	
	 public Long getHpId() {
		return hpId;
	}

	public void setHpId(Long hpId) {
		this.hpId = hpId;
	}

	public Byte getLoyaltyCustomer() {
		return loyaltyCustomer;
	}

	public void setLoyaltyCustomer(Byte loyaltyCustomer) {
		this.loyaltyCustomer = loyaltyCustomer;
	}
	/*public Object getScore() {
		return score;
	}
	
	public void setScore(Object score) {
		this.score = score;
	}
	*/
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		StringBuffer contactSb = new StringBuffer();
		contactSb.append(contactId);
		contactSb.append(", ");
		
		
		//String contactInfo = contactId + "," + mailingList + ", " + emailId + ", " + firstName + ", " + lastName + ", " + createdDate + ", " + optinStatus + ", " + emailStatus + ", " + lastStatusChange + ", " + lastMailDate + ", " + addressOne + ", " + addressTwo + ", " + city + ", " + state + ", " + country + ", " + pin + ", " + phone + ", " + optin + ", " + subscriptionType;
		String contactInfo = emailId + ", " + firstName + ", " + lastName + ", " + addressOne + ", " + addressTwo + ", " + city + ", " + state + ", " + country + ", " + pin + ", " + phone;
		return contactInfo;
	}
	
	
	
	public String getActivityDate() {
		return activityDate;
	}

	public void setActivityDate(String activityDate) {
		this.activityDate = activityDate;
	}

	public String getHomeStore() {
		return homeStore;
	}

	public void setHomeStore(String homeStore) {
		this.homeStore = homeStore;
	}

	public String getMobileStatus() {
		return mobileStatus;
	}

	public void setMobileStatus(String mobileStatus) {
		this.mobileStatus = mobileStatus;
	}

	

	public String getZip() {
		return zip;
	}

	public void setZip(String zip) {
		this.zip = zip;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public String getHomePhone() {
		return homePhone;
	}

	public void setHomePhone(String homePhone) {
		this.homePhone = homePhone;
	}


public Calendar getLastSMSDate() {
		return lastSMSDate;
	}

	public void setLastSMSDate(Calendar lastSMSDate) {
		this.lastSMSDate = lastSMSDate;
	}

	public boolean isMobileOptin() {
		return mobileOptin;
	}

	public void setMobileOptin(boolean mobileOptin) {
		this.mobileOptin = mobileOptin;
	}

	public Long getMlBits() {
		return mlBits;
	}

	public void setMlBits(Long mlBits) {
		this.mlBits = mlBits;
	}

	public String getCategories() {
		return categories;
	}

	public void setCategories(String categories) {
		this.categories = categories;
	}

	public int getLastMailSpan() {
		return lastMailSpan;
	}

	public void setLastMailSpan(int lastMailSpan) {
		this.lastMailSpan = lastMailSpan;
	}

	public int getLastSmsSpan() {
		return lastSmsSpan;
	}

	public void setLastSmsSpan(int lastSmsSpan) {
		this.lastSmsSpan = lastSmsSpan;
	}

	public String getLanguage() {
		return language;
	}


	public void setLanguage(String language) {
		this.language = language;
	}


	public Boolean getPushNotification() {
		return pushNotification;
	}


	public void setPushNotification(Boolean pushNotification) {
		this.pushNotification = pushNotification;
	}

	public String getInstanceId() {
		return instanceId;
	}

	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}

	public String getDeviceType() {
		return deviceType;
	}

	public void setDeviceType(String deviceType) {
		this.deviceType = deviceType;
	}
	public Calendar getModifiedDate() {
		return modifiedDate;
	}

	public void setModifiedDate(Calendar modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	@Override

	public Object clone() throws CloneNotSupportedException {
		 
		return super.clone();
		 
	}


}
