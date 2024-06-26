package org.mq.captiway.scheduler.beans;
// Generated 14 Oct, 2009 6:21:14 PM by Hibernate Tools 3.2.0.CR1


import java.util.Calendar;
import java.util.Date;
import java.util.Set;

/**
 * Users generated by hbm2java
 */
public class Users  implements java.io.Serializable {


     private Long userId;
     private String mqsId;
     private String userName;
     private String password;
     private String emailId;
     private String firstName;
     private String lastName;
     private String companyName;
     private Date createdDate;
     private String addressOne;
     private String addressTwo;
     private String city;
     private String state;
     private String country;
     private String pinCode;
     private String phone;
     private boolean enabled;
     private Vmta vmta;
     private Calendar packageStartDate;
     private Integer emailCount;
     private Integer usedEmailCount;
     private Integer smsCount;
     private Integer usedSmsCount;
     private Calendar packageExpiryDate;
     private Byte footerEditor;
     private String userSMSTool;
     /*private Byte countryCarrier;*/
     //Modified for UAE as carrier is out of Byte Range
     private Short countryCarrier;
     private String MsgChkType;

     private String clientTimeZone;

     private String token;
     private boolean digitalReceiptExtraction;
     private String countryType;

     private boolean considerSMSSettings;
     private boolean enableSMS;
     private Calendar lastLoggedInTime;
     private Byte optInMedium;

     private String weeklyReportEmailId;
     private String campExpEmailId;
     private boolean weeklyReportTypeEmail;
	 private boolean weeklyReportTypeSMS;

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

	private int weeklyReportDay;
  	
  	  private Date weeklyReportTime;

	public boolean isEnableSMS() {
		return enableSMS;
	}

	public void setEnableSMS(boolean enableSMS) {
		this.enableSMS = enableSMS;
	}

	public boolean isConsiderSMSSettings() {
		return considerSMSSettings;
	}

	public void setConsiderSMSSettings(boolean considerSMSSettings) {
		this.considerSMSSettings = considerSMSSettings;
	}

	public String getCountryType() {
		return countryType;
	}

	public void setCountryType(String countryType) {
		this.countryType = countryType;
	}

	public boolean isDigitalReceiptExtraction() {
  		return digitalReceiptExtraction;
  	}

  	public void setDigitalReceiptExtraction(boolean digitalReceiptExtraction) {
  		this.digitalReceiptExtraction = digitalReceiptExtraction;
  	}
  	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
   
	public String getClientTimeZone() {
		return clientTimeZone;
	}

	public void setClientTimeZone(String clientTimeZone) {
		this.clientTimeZone = clientTimeZone;
	}
 /* Commented For UAE 
	public Byte getCountryCarrier() {
		return countryCarrier;
	}

	public void setCountryCarrier(Byte countryCarrier) {
		this.countryCarrier = countryCarrier;
	}
*/
	//Modified for UAE as carrier is out of Byte Range
	public Short getCountryCarrier() {
		return countryCarrier;
	}

	public void setCountryCarrier(Short countryCarrier) {
		this.countryCarrier = countryCarrier;
	}
	
	public String getUserSMSTool() {
		return userSMSTool;
	}

	public void setUserSMSTool(String userSMSTool) {
		this.userSMSTool = userSMSTool;
	}

	private String accountType;
     
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

private boolean subscriptionEnable;
	public boolean getSubscriptionEnable() {
		return subscriptionEnable;
	}

	public void setSubscriptionEnable(boolean subscriptionEnable) {
		this.subscriptionEnable = subscriptionEnable;
	}



	public Users() {
    }

    public Users(String mqsId, String userName, String password, String emailId, String firstName, String lastName, String companyName, Date createdDate, String addressOne, String addressTwo, String city, String state, String country, String pinCode, String phone, boolean enabled) {
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
    public Date getCreatedDate() {
        return this.createdDate;
    }
    
    public void setCreatedDate(Date createdDate) {
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
    public boolean isEnabled() {
        return this.enabled;
    }
    
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

	public Vmta getVmta() {
		return vmta;
	}

	public void setVmta(Vmta vmta) {
		this.vmta = vmta;
	}
	
   public Calendar getPackageExpiryDate() {
        return this.packageExpiryDate;
    }
    
    public void setPackageExpiryDate(Calendar packageExpiryDate) {
    	this.packageExpiryDate = packageExpiryDate;
    }
    
	 public void setPackageStartDate(Calendar packageStartDate) {
		this.packageStartDate = packageStartDate;
	}

	public Calendar getPackageStartDate() {
		return packageStartDate;
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

	public Byte getFooterEditor() {
		return footerEditor;
	}

	public void setFooterEditor(Byte footerEditor) {
		this.footerEditor = footerEditor;
	}

	
	
	private String userDomainStr;
	public String getUserDomainStr() {
		
		Set<UsersDomains> domainSet = userDomains;
		
		userDomainStr = "";
		
		for (UsersDomains usersDomains : domainSet) {
			
			 if(userDomainStr.length()>0) userDomainStr+=",";
			 userDomainStr += usersDomains.getDomainName();
				 
		}
		
		return userDomainStr;
	}


	public void setUserDomainStr(String userDomainStr) {
		this.userDomainStr = userDomainStr;
	}

	public String getMsgChkType() {
		return MsgChkType;
	}

	public void setMsgChkType(String msgChkType) {
		MsgChkType = msgChkType;
	}
		
  	public Calendar getLastLoggedInTime() {
		return lastLoggedInTime;
	}

	public void setLastLoggedInTime(Calendar lastLoggedInTime) {
		this.lastLoggedInTime = lastLoggedInTime;
	}

	public Byte getOptInMedium() {
		return optInMedium;
	}
	public void setOptInMedium(Byte optInMedium) {
		this.optInMedium = optInMedium;
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

	public Date getWeeklyReportTime() {
		return weeklyReportTime;
	}

	public void setWeeklyReportTime(Date weeklyReportTime) {
		this.weeklyReportTime = weeklyReportTime;
	}

}//EOF


