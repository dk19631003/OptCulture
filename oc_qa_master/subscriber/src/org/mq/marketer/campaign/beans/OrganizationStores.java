package org.mq.marketer.campaign.beans;

import java.util.Calendar;

import org.mq.marketer.campaign.general.Constants;

public class OrganizationStores {
	private Long storeId;
	private String homeStoreId;
	private String storeName;
	private UserOrganization userOrganization;
	private String storeManagerName;
	private Calendar createdDate;
	private Calendar modifiedDate;
	private String emailId;
	private String website;
	
	private Address address;
	private String addressStr;
	
	 private boolean addressFlag;
	 
	 private String storeBrand;
	 private String locality;
	 private String city;
	 private String state;
	 private String country;
	 private String zipCode;
	 private String mobileNo;
	 private String latitude;
	 private String longitude;
	 private String storeImagePath;
	 private String brandImagePath;
	 private String description;
	 private String googleMapLink;
	 public String getGoogleMapLink() {
			return googleMapLink;
		}

		public void setGoogleMapLink(String googleMapLink) {
			this.googleMapLink = googleMapLink;
		}
	 
	 
	 public String getStoreBrand() {
		return storeBrand;
	}

	public void setStoreBrand(String storeBrand) {
		this.storeBrand = storeBrand;
	}

	public String getLocality() {
		return locality;
	}

	public void setLocality(String locality) {
		this.locality = locality;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getMobileNo() {
		return mobileNo;
	}

	public void setMobileNo(String mobileNo) {
		this.mobileNo = mobileNo;
	}

	private String fromEmailId;
	 private String replyToEmailId;
	 private String fromName;
     private String subsidiaryId;
     private Long domainId;
	private String subsidiaryName;
	private String ERPStoreId;
        private String timeZone;
	
	public String getERPStoreId() {
		return ERPStoreId;
	}

	public void setERPStoreId(String eRPStoreId) {
		ERPStoreId = eRPStoreId;
	}

	public String getSubsidiaryId() {
		return subsidiaryId;
	}

	public void setSubsidiaryId(String subsidiaryId) {
		this.subsidiaryId = subsidiaryId;
	}

	public String getSubsidiaryName() {
		return subsidiaryName;
	}

	public void setSubsidiaryName(String subsidiaryName) {
		this.subsidiaryName = subsidiaryName;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}

	public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}

	public String getReplyToEmailId() {
		return replyToEmailId;
	}

	public void setReplyToEmailId(String replyToEmailId) {
		this.replyToEmailId = replyToEmailId;
	}

	public String getFromEmailId() {
		return fromEmailId;
	}

	public void setFromEmailId(String fromEmailId) {
		this.fromEmailId = fromEmailId;
	}

	public OrganizationStores(){}
	
	public Long getStoreId() {
		return storeId;
	}
	public void setStoreId(Long storeId) {
		this.storeId = storeId;
	}
	public String getHomeStoreId() {
		return homeStoreId;
	}
	public void setHomeStoreId(String homeStoreId) {
		this.homeStoreId = homeStoreId;
	}
	public String getStoreName() {
		return storeName;
	}
	public void setStoreName(String storeName) {
		this.storeName = storeName;
	}
	public UserOrganization getUserOrganization() {
		return userOrganization;
	}
	public void setUserOrganization(UserOrganization userOrganization) {
		this.userOrganization = userOrganization;
	}
	public String getStoreManagerName() {
		return storeManagerName;
	}
	public void setStoreManagerName(String storeManagerName) {
		this.storeManagerName = storeManagerName;
	}
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public Calendar getModifiedDate() {
		return modifiedDate;
	}
	public void setModifiedDate(Calendar modifiedDate) {
		this.modifiedDate = modifiedDate;
	}
	public String getEmailId() {
		return emailId;
	}
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	public String getWebsite() {
		return website;
	}
	public void setWebsite(String website) {
		this.website = website;
	}

	
	 public boolean isAddressFlag() {
	        return this.addressFlag;
	    }
	    
    public void setAddressFlag(boolean addressFlag) {
        this.addressFlag = addressFlag;
    }
	
	
	
	
	
    public Address getAddress() {
		
		if(address == null) {
			
			address = new Address();
			try {
				
				if(addressStr == null)	return address;
				
				String[] addrArr = addressStr.split(Constants.ADDR_COL_DELIMETER);
				
				address.setAddressOne((addrArr[0]== null)?"":addrArr[0]);
				address.setAddressTwo((addrArr[1]== null)?"":addrArr[1]);
				address.setCity((addrArr[2]== null)?"":addrArr[2]);
				address.setState((addrArr[3] == null)?"":addrArr[3]);
				address.setCountry((addrArr[4] == null)?"":addrArr[4]);
				
				if(addrArr.length > 5 && addrArr[5] != null) {
					address.setPin((addrArr[5]));
				}
				
				if(addrArr.length > 6 ) {
					address.setPhone((addrArr[6] == null)?"":addrArr[6]);
				}
			} catch (NumberFormatException e) {
				
			} catch (ArrayIndexOutOfBoundsException iob) {
				
			} catch (Exception ex) {
				
			}
		}
		return address;
	}


	public void setAddress(Address address) {
		this.address = address;
		StringBuffer sb = new StringBuffer();
		
		sb.append(((address.getAddressOne() == null)?"": address.getAddressOne()));
		sb.append(Constants.ADDR_COL_DELIMETER+((address.getAddressTwo() == null) ?"":address.getAddressTwo()));
		sb.append(Constants.ADDR_COL_DELIMETER+((address.getCity() == null)?"":address.getCity()));
		sb.append(Constants.ADDR_COL_DELIMETER+((address.getState() == null)?"":address.getState()));
		sb.append(Constants.ADDR_COL_DELIMETER+((address.getCountry() == null)?"":address.getCountry()));
		sb.append(Constants.ADDR_COL_DELIMETER+((address.getPin() == null)?"":address.getPin()));
		sb.append(Constants.ADDR_COL_DELIMETER+((address.getPhone() == null)?"":address.getPhone()));
		this.addressStr = sb.toString();
	}


	public String getAddressStr() {
		return addressStr;
	}


	public void setAddressStr(String addressStr) {
		this.addressStr = addressStr;
	}

	
	
	public String getTimeZone() {
		return timeZone;
	}

	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}


	public String getStoreImagePath() {
		return storeImagePath;
	}

	public void setStoreImagePath(String storeImagePath) {
		this.storeImagePath = storeImagePath;
	}

	public String getBrandImagePath() {
		return brandImagePath;
	}

	public void setBrandImagePath(String brandImagePath) {
		this.brandImagePath = brandImagePath;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	
	
}
