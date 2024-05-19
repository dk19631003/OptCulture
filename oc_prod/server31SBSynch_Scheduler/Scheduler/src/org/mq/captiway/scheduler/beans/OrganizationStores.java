package org.mq.captiway.scheduler.beans;

import java.util.Calendar;

import org.mq.captiway.scheduler.utility.Constants;


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
	 private String fromEmailId;
	 
	 public String getFromName() {
		return fromName;
	}

	public void setFromName(String fromName) {
		this.fromName = fromName;
	}


	private String fromName;
	 public String getReplyToEmailId() {
		return replyToEmailId;
	}

	public void setReplyToEmailId(String replyToEmailId) {
		this.replyToEmailId = replyToEmailId;
	}


	private String replyToEmailId;
	
	
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
//					address.setPin(Long.parseLong(addrArr[5]));
					address.setPin(addrArr[5]);
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
		sb.append(org.mq.captiway.scheduler.utility.Constants.ADDR_COL_DELIMETER+((address.getAddressTwo() == null) ?"":address.getAddressTwo()));
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

	
	
	
	
	
	

	
	
	
}
