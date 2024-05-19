package org.mq.marketer.campaign.beans;
// Generated 14 Oct, 2009 6:21:14 PM by Hibernate Tools 3.2.0.CR1


import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Users generated by hbm2java
 */
public class Users  implements java.io.Serializable, Comparable<Users> {


	private static final long serialVersionUID = 1L; 
        private Long userId;
	private String mqsId;
	private String userName;
	private String password;
	private String emailId;
	private String firstName;
	private String lastName;
	private String companyName;
	private Calendar createdDate;
	private String addressOne;
	private String addressTwo;
	private String city;
	private String state;
	private String country;
	private String pinCode;
	private String phone;
	private Double excludeDiscPerc;
	private boolean allowIssuanceOnOfferItem;// //APP-4657 - allow issuance for 1+1 offer item i.e even if we have exclude discount item
											//flag enable and itemPromoDiscount is coming as 1, allow issuance on that item

	public boolean isAllowIssuanceOnOfferItem() {
		return allowIssuanceOnOfferItem;
	}

	public void setAllowIssuanceOnOfferItem(boolean allowIssuanceOnOfferItem) {
		this.allowIssuanceOnOfferItem = allowIssuanceOnOfferItem;
	}
	public Double getExcludeDiscPerc() {
		return excludeDiscPerc;
	}

	public void setExcludeDiscPerc(Double excludeDiscPerc) {
		this.excludeDiscPerc = excludeDiscPerc;
	}


	//private String vmta;
	private Vmta vmta;
	private boolean enabled;
	private Integer emailCount;
	private Integer usedEmailCount;
	private Integer smsCount;
	private Integer usedSmsCount;
	private Calendar packageStartDate;
	private Calendar packageExpiryDate;
	private String userActivitySettings;
	private String token;
	private Byte footerEditor;
	
	
	private boolean zoneWise;

	private String accountType;
    
	
	private String userDomainStr;
	private String userRole;
	private String CIMProfileId;
	private String MsgChkType;
	private String userSMSTool;
    //private Byte countryCarrier;
	private Short countryCarrier;
//    private String countryCode;
    private String clientTimeZone;
	private boolean subscriptionEnable;
	private Calendar lastLoggedInTime;
	private Calendar mandatoryUpdatePwdOn;
	private boolean ignoreissuanceOnRedemption;
	public boolean isIgnoreissuanceOnRedemption() {
		return ignoreissuanceOnRedemption;
	}

	public void setIgnoreissuanceOnRedemption(boolean ignoreissuanceOnRedemption) {
		this.ignoreissuanceOnRedemption = ignoreissuanceOnRedemption;
	}


		//APP-3535
		private boolean receiptOnWA =false;
		private String WAAPIKey;
		private String WAAPIEndPointURL;
		private String WATemplateID;
		private String WAUserID;
		private String WAJSONTemplate;
		
		private boolean enableNPS =false;
		private String NPSProductKey;
		private String NPSEndPointURL;
		private String NPSJSONTemplate;
		private String NPSCookie;
		
		public String getNPSCookie() {
			return NPSCookie;
		}

		public void setNPSCookie(String nPSCookie) {
			NPSCookie = nPSCookie;
		}

		public boolean isEnableNPS() {
			return enableNPS;
		}

		public void setEnableNPS(boolean enableNPS) {
			this.enableNPS = enableNPS;
		}

		public String getNPSProductKey() {
			return NPSProductKey;
		}

		public void setNPSProductKey(String nPSProductKey) {
			NPSProductKey = nPSProductKey;
		}

		public String getNPSEndPointURL() {
			return NPSEndPointURL;
		}

		public void setNPSEndPointURL(String nPSEndPointURL) {
			NPSEndPointURL = nPSEndPointURL;
		}

		public String getNPSJSONTemplate() {
			return NPSJSONTemplate;
		}

		public void setNPSJSONTemplate(String nPSJSONTemplate) {
			NPSJSONTemplate = nPSJSONTemplate;
		}
		
		public String getWAJSONTemplate() {
			return WAJSONTemplate;
		}

		public void setWAJSONTemplate(String wAJSONTemplate) {
			WAJSONTemplate = wAJSONTemplate;
		}
		public String getWAAPIKey() {
			return WAAPIKey;
		}

		public void setWAAPIKey(String wAAPIKey) {
			WAAPIKey = wAAPIKey;
		}

		public String getWAAPIEndPointURL() {
			return WAAPIEndPointURL;
		}

		public void setWAAPIEndPointURL(String wAAPIEndPointURL) {
			WAAPIEndPointURL = wAAPIEndPointURL;
		}

		public String getWATemplateID() {
			return WATemplateID;
		}

		public void setWATemplateID(String wATemplateID) {
			WATemplateID = wATemplateID;
		}

		public String getWAUserID() {
			return WAUserID;
		}

		public void setWAUserID(String wAUserID) {
			WAUserID = wAUserID;
		}
		public boolean isReceiptOnWA() {
			return receiptOnWA;
		}

		public void setReceiptOnWA(boolean receiptOnWA) {
			this.receiptOnWA = receiptOnWA;
		}
	
	
	//for items validation in DR
	public boolean validateItemsInReturnTrx;
	public boolean ignorePointsRedemption=true;
private boolean redemptionAsDiscount = false;
	public boolean isRedemptionAsDiscount() {
		return redemptionAsDiscount;
	}

	public void setRedemptionAsDiscount(boolean redemptionAsDiscount) {
		this.redemptionAsDiscount = redemptionAsDiscount;
	}
	private boolean allowBothDiscounts=false;
	public boolean isAllowBothDiscounts() {
		return allowBothDiscounts;
	}

	public void setAllowBothDiscounts(boolean allowBothDiscounts) {
		this.allowBothDiscounts = allowBothDiscounts;
	}	
	private boolean newPlugin;
	
	//contract details
	private String contractStores;
	private String contractContacts;
	private String contractSMSAdded;
	private String contractEReceiptsRestricted;
	private String redeemTenderDispLabel;
	private boolean receiptOnSMS =false;
	//temporary fields to configure user's specific DRSMS content with Equence
		private String DRSMSContent;
		private String DRSMSTempRegID;
		private String DRSMSSenderID;
		
		public String getDRSMSContent() {
			return DRSMSContent;
		}

		public void setDRSMSContent(String dRSMSContent) {
			DRSMSContent = dRSMSContent;
		}

		public String getDRSMSTempRegID() {
			return DRSMSTempRegID;
		}

		public void setDRSMSTempRegID(String dRSMSTempRegID) {
			DRSMSTempRegID = dRSMSTempRegID;
		}

		public String getDRSMSSenderID() {
			return DRSMSSenderID;
		}

		public void setDRSMSSenderID(String dRSMSSenderID) {
			DRSMSSenderID = dRSMSSenderID;
		}
private boolean ignoretrxUpOnExtraction = true;//
	
	public boolean isIgnoretrxUpOnExtraction() {
		return ignoretrxUpOnExtraction;
	}

	public void setIgnoretrxUpOnExtraction(boolean ignoretrxUpOnExtraction) {
		this.ignoretrxUpOnExtraction = ignoretrxUpOnExtraction;
	}
	public String getRedeemTenderDispLabel() {
		return redeemTenderDispLabel;
	}

	public void setRedeemTenderDispLabel(String redeemTenderDispLabel) {
		this.redeemTenderDispLabel = redeemTenderDispLabel;
	}

	public boolean isNewPlugin() {
		return newPlugin;
	}

	public void setNewPlugin(boolean newPlugin) {
		this.newPlugin = newPlugin;
	}

	public Calendar getMandatoryUpdatePwdOn() {
		return mandatoryUpdatePwdOn;
	}

	public void setMandatoryUpdatePwdOn(Calendar mandatoryUpdatePwdOn) {
		this.mandatoryUpdatePwdOn = mandatoryUpdatePwdOn;
	}

	// Added for sms 
	private boolean enableSMS;
	private boolean considerSMSSettings;
	private String countryType;
	private Byte optInMedium;
	//APP-4288
	private boolean enableWA;
	private boolean enableSmartEReceipt;
	
	public boolean isEnableSmartEReceipt() {
		return enableSmartEReceipt;
	}

	public void setEnableSmartEReceipt(boolean enableSmartEReceipt) {
		this.enableSmartEReceipt = enableSmartEReceipt;
	}

	public boolean isEnableWA() {
		return enableWA;
	}

	public void setEnableWA(boolean enableWA) {
		this.enableWA = enableWA;
	}


	private String weeklyReportEmailId;
	
	private int weeklyReportDay;
	
	private Date weeklyReportTime;
	private String tempPwd;
	
	 public String getTempPwd() {
		return tempPwd;
	}

	public void setTempPwd(String tempPwd) {
		this.tempPwd = tempPwd;
	}

	private String campExpEmailId;
	 private boolean enableUnsublink;
	 private String unsuburl;
	 private boolean weeklyReportTypeEmail;
	 private boolean weeklyReportTypeSMS;
	 private String POSVersion;
	 
	 private String optinRoute;
	 private boolean optinMobileByDefault;
	 //Loyalty Service Type
	 
	 public boolean isOptinMobileByDefault() {
		return optinMobileByDefault;
	}

	public void setOptinMobileByDefault(boolean optinMobileByDefault) {
		this.optinMobileByDefault = optinMobileByDefault;
	}

	public String getOptinRoute() {
		return optinRoute;
	}

	public void setOptinRoute(String optinRoute) {
		this.optinRoute = optinRoute;
	}

	public String getPOSVersion() {
		return POSVersion;
	}

	public void setPOSVersion(String pOSVersion) {
		POSVersion = pOSVersion;
	}

	private String loyaltyServicetype;
	private boolean sendExpiryInfo;
	private String selectedExpiryInfoType;
	public String getSelectedExpiryInfoType() {
		return selectedExpiryInfoType;
	}

	public void setSelectedExpiryInfoType(String selectedExpiryInfoType) {
		this.selectedExpiryInfoType = selectedExpiryInfoType;
	}

	public boolean isSendExpiryInfo() {
		return sendExpiryInfo;
	}

	public void setSendExpiryInfo(boolean sendExpiryInfo) {
		this.sendExpiryInfo = sendExpiryInfo;
	}



	public String getloyaltyServicetype() {
		return loyaltyServicetype;
	}

	public void setloyaltyServicetype(String loyaltyServicetype) {
		this.loyaltyServicetype = loyaltyServicetype;
	}

	public boolean isWeeklyReportTypeEmail() {
		return weeklyReportTypeEmail;
	}

	public void setWeeklyReportTypeEmail(boolean weeklyReportTypeEmail) {
		this.weeklyReportTypeEmail = weeklyReportTypeEmail;
	}

	public boolean isWeeklyReportTypeSMS() {
		return weeklyReportTypeSMS;
	}

	public void setWeeklyReportTypeSMS(boolean weeklyReportTypeSMS) {
		this.weeklyReportTypeSMS = weeklyReportTypeSMS;
	}

	public String getCampExpEmailId() {
		return campExpEmailId;
	}

	public void setCampExpEmailId(String campExpEmailId) {
		this.campExpEmailId = campExpEmailId;
	}
	
	public String getCountryType() {
		return countryType;
	}

	public void setCountryType(String countryType) {
		this.countryType = countryType;
	}

		
	public Byte getOptInMedium() {
		return optInMedium;
	}
	public void setOptInMedium(Byte optInMedium) {
		this.optInMedium = optInMedium;
	}	
		
	public boolean isConsiderSMSSettings() {
		return considerSMSSettings;
	}

	public void setConsiderSMSSettings(boolean considerSMSSettings) {
		this.considerSMSSettings = considerSMSSettings;
	}

	public boolean getSubscriptionEnable() {
		return subscriptionEnable;
	}

	public void setSubscriptionEnable(boolean subscriptionEnable) {
		this.subscriptionEnable = subscriptionEnable;
	}
    
	public String getClientTimeZone() {
		return clientTimeZone;
	}

	public void setClientTimeZone(String clientTimeZone) {
		this.clientTimeZone = clientTimeZone;
	}
	public String getCIMProfileId() {
		return CIMProfileId;
	}

	public void setCIMProfileId(String cIMProfileId) {
		CIMProfileId = cIMProfileId;
	}

	private boolean digitalReceiptExtraction;
	
	
	public boolean isDigitalReceiptExtraction() {
		return digitalReceiptExtraction;
	}

	public void setDigitalReceiptExtraction(boolean digitalReceiptExtraction) {
		this.digitalReceiptExtraction = digitalReceiptExtraction;
	}
	private boolean showOnlyHighestLtyDC = false;
	public boolean isShowOnlyHighestLtyDC() {
		return showOnlyHighestLtyDC;
	}

	public void setShowOnlyHighestLtyDC(boolean showOnlyHighestLtyDC) {
		this.showOnlyHighestLtyDC = showOnlyHighestLtyDC;
	}
	private boolean showOnlyHighestDiscReceiptDC=true;

	public boolean isShowOnlyHighestDiscReceiptDC() {
	return showOnlyHighestDiscReceiptDC;
	}

	public void setShowOnlyHighestDiscReceiptDC(boolean showOnlyHighestDiscReceiptDC) {
	this.showOnlyHighestDiscReceiptDC = showOnlyHighestDiscReceiptDC;
	}

	private Set<SecRoles> roles = new HashSet<SecRoles>();
	    	    
	   
	public Set<SecRoles> getRoles() {
		return roles;
	}

	public void setRoles(Set<SecRoles> roles) {
		this.roles = roles;
	}
		
		
	public String getUserDomainStr() {
		
		/*Set<UsersDomains> domainSet = userDomains;
		
		userDomainStr = "";
		
		for (UsersDomains usersDomains : domainSet) {
			
			 if(userDomainStr.length()>0) userDomainStr+=",";
			 userDomainStr += usersDomains.getDomainName();
				 
		}*/
		
		return userDomainStr;
	}

	public void setUserDomainStr(String userDomainStr) {
		this.userDomainStr = userDomainStr;
	}

	public String getUserRole() {
		return userRole;
	}

	public void setUserRole(String userRole) {
		this.userRole = userRole;
	}

	

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	private UserOrganization userOrganization;
	
	public UserOrganization getUserOrganization() {
		return userOrganization;
	}

	public void setUserOrganization(UserOrganization userOrganization) {
		this.userOrganization = userOrganization;
	}

	private BigInteger userActivityAsBigInteger;
	
	private Set<UsersDomains> userDomains;
	private Users parentUser;

	

	public Set<UsersDomains> getUserDomains() {
		return userDomains;
	}

	public void setUserDomains(Set<UsersDomains> userDomains) {
		this.userDomains = userDomains;
	}

	public Users getParentUser() {
		return parentUser;
	}

	public void setParentUser(Users parentUser) {
		this.parentUser = parentUser;
	}

	public Users() {
    }

    public Users(String mqsId, String userName, String password, String emailId,
    		String firstName, String lastName, String companyName, Calendar createdDate, 
    		String addressOne, String addressTwo, String city, String state, String country,
    		String pinCode, String phone, boolean enabled) {
    	
       this.mqsId = mqsId;
       this.userName = userName;
       this.password = password;
       this.emailId = emailId;
       this.firstName = firstName;
       this.lastName = lastName;
       this.companyName = companyName;
       this.createdDate = createdDate;
       this.addressOne = addressOne;
       this.addressTwo = addressTwo;
       this.city = city;
       this.state = state;
       this.country = country;
       this.pinCode = pinCode;
       this.phone = phone;
       this.enabled = enabled;
    }
   
    public Long getUserId() {
        return this.userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }
    public String getMqsId() {
        return this.mqsId;
    }
    
    public void setMqsId(String mqsId) {
        this.mqsId = mqsId;
    }
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(String userName) {
        this.userName = userName;
    }
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
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
    public String getCompanyName() {
        return this.companyName;
    }
    
    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }
    public Calendar getCreatedDate() {
        return this.createdDate;
    }
    
    public void setCreatedDate(Calendar createdDate) {
        this.createdDate = createdDate;
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
    public String getPinCode() {
        return this.pinCode;
    }
    
    public void setPinCode(String pinCode) {
        this.pinCode = pinCode;
    }
    public String getPhone() {
        return this.phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    /*public String getVmta() {
		return vmta;
	}

	public void setVmta(String vmta) {
		this.vmta = vmta;
	}*/

	public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    
    public void setPackageStartDate(Calendar packageStartDate) {
		this.packageStartDate = packageStartDate;
	}

	public Calendar getPackageStartDate() {
		return packageStartDate;
	}

	public Calendar getPackageExpiryDate() {
        return this.packageExpiryDate;
    }
    
    public void setPackageExpiryDate(Calendar packageExpiryDate) {
    	this.packageExpiryDate = packageExpiryDate;
    }
    
    public void setEmailCount(Integer emailCount) {
    	this.emailCount = emailCount;
    }
    
    public Integer getEmailCount() {
    	return this.emailCount;
    }
    
    public void setUsedEmailCount(Integer usedEmailCount) {
    	this.usedEmailCount = usedEmailCount;
    }
    
    public Integer getUsedEmailCount() {
    	return this.usedEmailCount;
    }
    
    public Integer getSmsCount() {
		return smsCount;
	}

	public void setSmsCount(Integer smsCount) {
		this.smsCount = smsCount;
	}

	public Integer getUsedSmsCount() {
		return usedSmsCount;
	}

	public void setUsedSmsCount(Integer usedSmsCount) {
		this.usedSmsCount = usedSmsCount;
	}

    
	public String getUserActivitySettings() {
		return userActivitySettings;
	}

	public void setUserActivitySettings(String userActivitySettings) {
		
		this.userActivitySettings = userActivitySettings;
		
		if(this.userActivitySettings != null) {			
			this.userActivityAsBigInteger =  new BigInteger(this.userActivitySettings, 16);
		}
	}
	
	public Byte getFooterEditor() {
		return footerEditor;
	}

	public void setFooterEditor(Byte footerEditor) {
		this.footerEditor = footerEditor;
	}

	public BigInteger getActivityAsBigInteger() {
		return userActivityAsBigInteger;
	}
	
	
	public String getAddress() {
		StringBuffer address = new StringBuffer();
		if(this.getAddressOne() != null ) address.append("  " + this.getAddressOne());
		if(this.getAddressTwo() != null ) address.append("  " + this.getAddressTwo());
		if(this.getCity() != null ) address.append("  " + this.getCity());
		if(this.getState() != null ) address.append("  " + this.getState());
		if(this.getCountry() != null ) address.append("  " + this.getCountry());
		if(this.getPinCode() != null ) address.append("  " + this.getPinCode());
		return address.toString();
	}
	
	

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	@Override
	public int compareTo(Users user) {
		// TODO Auto-generated method stub
		return user.getUserName().compareTo(this.userName);
	}

	/*public boolean isTransactionalMsgChk() {
		return transactionalMsgChk;
	}

	public void setTransactionalMsgChk(boolean transactionalMsgChk) {
		this.transactionalMsgChk = transactionalMsgChk;
	}*/

	public String getUserSMSTool() {
		return userSMSTool;
	}

	public void setUserSMSTool(String userSMSTool) {
		this.userSMSTool = userSMSTool;
	}

	/*public Byte getCountryCarrier() {
		return countryCarrier;
	}*/
	public Short getCountryCarrier() {
		return countryCarrier;
	}

	/*public void setCountryCarrier(Byte countryCarrier) {
		this.countryCarrier = countryCarrier;
	}*/
	public void setCountryCarrier(Short countryCarrier) {
		this.countryCarrier = countryCarrier;
	}
	public String getMsgChkType() {
		return MsgChkType;
	}

	public void setMsgChkType(String msgChkType) {
		MsgChkType = msgChkType;
	}

	

	public BigInteger getUserActivityAsBigInteger() {
		return userActivityAsBigInteger;
	}

	public void setUserActivityAsBigInteger(BigInteger userActivityAsBigInteger) {
		this.userActivityAsBigInteger = userActivityAsBigInteger;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	public boolean isEnableSMS() {
		return enableSMS;
	}

	public void setEnableSMS(boolean enableSMS) {
		this.enableSMS = enableSMS;
	}
	
	public Calendar getLastLoggedInTime() {
		return lastLoggedInTime;
	}

	public void setLastLoggedInTime(Calendar lastLoggedInTime) {
		this.lastLoggedInTime = lastLoggedInTime;
	}

	public Date getWeeklyReportTime() {
		return weeklyReportTime;
	}

	public void setWeeklyReportTime(Date weeklyReportTime) {
		this.weeklyReportTime = weeklyReportTime;
	}

	public String getWeeklyReportEmailId() {
		return weeklyReportEmailId;
	}

	public void setWeeklyReportEmailId(String weeklyReportEmailId) {
		this.weeklyReportEmailId = weeklyReportEmailId;
	}

	public int getWeeklyReportDay() {
		return weeklyReportDay;
	}

	public void setWeeklyReportDay(int weeklyReportDay) {
		this.weeklyReportDay = weeklyReportDay;
	}
	
	
	public Vmta getVmta() {
		return vmta;
	}

	public void setVmta(Vmta vmta) {
		this.vmta = vmta;
	}

	public boolean isEnableUnsublink() {
		return enableUnsublink;
	}

	public void setEnableUnsublink(boolean enableUnsublink) {
		this.enableUnsublink = enableUnsublink;
	}

	public String getUnsuburl() {
		return unsuburl;
	}

	public void setUnsuburl(String unsuburl) {
		this.unsuburl = unsuburl;
	}

	public boolean isZoneWise() {
		return zoneWise;
	}

	public void setZoneWise(boolean zoneWise) {
		this.zoneWise = zoneWise;
	}
	
	public boolean enableLoyaltyExtraction;
	public boolean excludeGiftRedemption;
	
	public boolean isExcludeGiftRedemption() {
		return excludeGiftRedemption;
	}

	public void setExcludeGiftRedemption(boolean excludeGiftRedemption) {
		this.excludeGiftRedemption = excludeGiftRedemption;
	}


	public boolean excludeDiscountedItem;
	public boolean isExcludeDiscountedItem() {
		return excludeDiscountedItem;
	}

	public void setExcludeDiscountedItem(boolean excludeDiscountedItem) {
		this.excludeDiscountedItem = excludeDiscountedItem;
	}

	public boolean isEnableLoyaltyExtraction() {
		return enableLoyaltyExtraction;
	}

	public void setEnableLoyaltyExtraction(boolean enableLoyaltyExtraction) {
		this.enableLoyaltyExtraction = enableLoyaltyExtraction;
	}

	public boolean enablePromoRedemption;
	public String itemNoteUsed;
	public String receiptNoteUsed;
	public String itemInfo;
	public String cardInfo;
	
	public String getCardInfo() {
		return cardInfo;
	}

	public void setCardInfo(String cardInfo) {
		this.cardInfo = cardInfo;
	}

	public boolean isEnablePromoRedemption() {
		return enablePromoRedemption;
	}

	public void setEnablePromoRedemption(boolean enablePromoRedemption) {
		this.enablePromoRedemption = enablePromoRedemption;
	}

	public String getItemNoteUsed() {
		return itemNoteUsed;
	}

	public void setItemNoteUsed(String itemNoteUsed) {
		this.itemNoteUsed = itemNoteUsed;
	}

	public String getReceiptNoteUsed() {
		return receiptNoteUsed;
	}

	public void setReceiptNoteUsed(String receiptNoteUsed) {
		this.receiptNoteUsed = receiptNoteUsed;
	}

	public String getItemInfo() {
		return itemInfo;
	}
	public void setItemInfo(String itemInfo) {
		this.itemInfo = itemInfo;
	}


	
	//public boolean enrollAllRequests;
	public boolean enrollFromDR;
	public boolean issuanceFromDR;
	public boolean returnFromDR;
	public boolean redemptionFromDR;
	
	public String redeemTender;
	public String nonInventoryItem;
	public boolean performRedeemedAmountReversal;
	
	public boolean isEnrollFromDR() {
		return enrollFromDR;
	}

	public void setEnrollFromDR(boolean enrollFromDR) {
		this.enrollFromDR = enrollFromDR;
	}

	
	public boolean isIssuanceFromDR() {
		return issuanceFromDR;
	}

	public void setIssuanceFromDR(boolean issuanceFromDR) {
		this.issuanceFromDR = issuanceFromDR;
	}

	public boolean isRedemptionFromDR() {
		return redemptionFromDR;
	}

	public void setRedemptionFromDR(boolean redemptionFromDR) {
		this.redemptionFromDR = redemptionFromDR;
	}

	/*public boolean isEnrollAllRequests() {
		return enrollAllRequests;
	}

	public void setEnrollAllRequests(boolean enrollAllRequests) {
		this.enrollAllRequests = enrollAllRequests;
	}*/

	public String getRedeemTender() {
		return redeemTender;
	}

	public void setRedeemTender(String redeemTender) {
		this.redeemTender = redeemTender;
	}

	public String getNonInventoryItem() {
		return nonInventoryItem;
	}

	public void setNonInventoryItem(String nonInventoryItem) {
		this.nonInventoryItem = nonInventoryItem;
	}

	public boolean isReturnFromDR() {
		return returnFromDR;
	}

	public void setReturnFromDR(boolean returnFromDR) {
		this.returnFromDR = returnFromDR;
	}

	public boolean isPerformRedeemedAmountReversal() {
		return performRedeemedAmountReversal;
	}

	public void setPerformRedeemedAmountReversal(boolean performRedeemedAmountReversal) {
		this.performRedeemedAmountReversal = performRedeemedAmountReversal;
	}

	private boolean specificDir;
	public boolean isSpecificDir() {
		return specificDir;
	}
	public void setSpecificDir(boolean specificDir) {
		this.specificDir = specificDir;
	}

	public boolean isValidateItemsInReturnTrx() {
		return validateItemsInReturnTrx;
	}

	public void setValidateItemsInReturnTrx(boolean validateItemsInReturnTrx) {
		this.validateItemsInReturnTrx = validateItemsInReturnTrx;
	}

	public boolean isIgnorePointsRedemption() {
		return ignorePointsRedemption;
	}

	public void setIgnorePointsRedemption(boolean ignorePointsRedemption) {
		this.ignorePointsRedemption = ignorePointsRedemption;
	}

	public String getContractStores() {
		return contractStores;
	}

	public void setContractStores(String contractStores) {
		this.contractStores = contractStores;
	}

	public String getContractContacts() {
		return contractContacts;
	}

	public void setContractContacts(String contractContacts) {
		this.contractContacts = contractContacts;
	}

	public String getContractSMSAdded() {
		return contractSMSAdded;
	}

	public void setContractSMSAdded(String contractSMSAdded) {
		this.contractSMSAdded = contractSMSAdded;
	}

	public String getContractEReceiptsRestricted() {
		return contractEReceiptsRestricted;
	}

	public void setContractEReceiptsRestricted(String contractEReceiptsRestricted) {
		this.contractEReceiptsRestricted = contractEReceiptsRestricted;
	}

	public boolean isReceiptOnSMS() {
		return receiptOnSMS;
	}

	public void setReceiptOnSMS(boolean receiptOnSMS) {
		this.receiptOnSMS = receiptOnSMS;
	}
	
	/*public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode(String countryCode) {
		this.countryCode = countryCode;
	}
*/
	private boolean coOnWA;	//app-3784 
	private String confirmOrderJSONTemplate;
	private String coCookie;
	public boolean isCoOnWA() {
		return coOnWA;
	}

	public void setCoOnWA(boolean coOnWA) {
		this.coOnWA = coOnWA;
	}

	public String getConfirmOrderJSONTemplate() {
		return confirmOrderJSONTemplate;
	}

	public void setConfirmOrderJSONTemplate(String confirmOrderJSONTemplate) {
		this.confirmOrderJSONTemplate = confirmOrderJSONTemplate;
	}

	public String getCoCookie() {
		return coCookie;
	}

	public void setCoCookie(String coCookie) {
		this.coCookie = coCookie;
	}
	
	
	
	
	
	
	
	
	
}


