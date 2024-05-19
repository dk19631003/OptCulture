package org.mq.marketer.campaign.beans;

public class ProductsUsed  implements java.io.Serializable {
	Long productId;
	Long numberOfContractedEmails;
    Long numberOfContractedSMS;
    String posSystemVersion;
    String optDigitalRecieptVersion;
    String optLoyaltyVersion;
    String optPromoVersion;
    String optIntelVersion;
    String optSyncVersion;
   	Long userId;
    
	public Long getProductId() {
		return productId;
	}
	public void setProductId(Long productId) {
		this.productId = productId;
	}
	
	public Long getNumberOfContractedEmails() {
		return numberOfContractedEmails;
	}
	public void setNumberOfContractedEmails(Long numberOfContractedEmails) {
		this.numberOfContractedEmails = numberOfContractedEmails;
	}
	
	public Long getNumberOfContractedSMS() {
		return numberOfContractedSMS;
	}
	public void setNumberOfContractedSMS(Long numberOfContractedSMS) {
		this.numberOfContractedSMS = numberOfContractedSMS;
	}
	
	public String getPOSSystemVersion() {
		return posSystemVersion;
	}
	public void setPOSSystemVersion(String posSystemVersion) {
		this.posSystemVersion = posSystemVersion;
	}
	
	public String getOptDigitalRecieptVersion() {
		return optDigitalRecieptVersion;
	}
	public void setOptDigitalRecieptVersion(String optDigitalRecieptVersion) {
		this.optDigitalRecieptVersion = optDigitalRecieptVersion;
	}
	
	public String getOptLoyaltyVersion() {
		return optLoyaltyVersion;
	}
	public void setOptLoyaltyVersion(String optLoyaltyVersion) {
		this.optLoyaltyVersion = optLoyaltyVersion;
	}
	
	public String getOptPromoVersion() {
		return optPromoVersion;
	}
	public void setOptPromoVersion(String optPromoVersion) {
		this.optPromoVersion = optPromoVersion;
	}
	
	public String getOptIntelVersion() {
		return optIntelVersion;
	}
	public void setOptIntelVersion(String optIntelVersion) {
		this.optIntelVersion = optIntelVersion;
	}
	
	 public String getOptSyncVersion() {
			return optSyncVersion;
		}
	 public void setOptSyncVersion(String optSyncVersion) {
			this.optSyncVersion = optSyncVersion;
		}
	
	
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
    
}
