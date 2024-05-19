package org.mq.captiway.scheduler.beans;

import java.util.Calendar;
import java.util.Date;
import java.util.Set;

import org.mq.captiway.scheduler.beans.UserOrganization;

public class SparkBaseLocationDetails  implements java.io.Serializable {
	private Long sparkBaseLocationDetails_id;
	private String	batchId;
	private	String clientId;
	private	String externalId;
	private	String initiatorId;
	private	String initiatorPassword;
	private String	initiatorType;
	private String	integrationPassword;
	private String	integrationUserName;
	private String localeId;
	private String locationId;
	private String	systemId;
	private String	terminalId;
	//sparkbase location settings created userId
	private Long userId;
	private Calendar createdDate;
	private boolean enabled;
	private boolean enableAlerts;
	private boolean emailAlerts;
	private boolean smsAlerts;
	private String countType;
	private String countValue;
	private Calendar loyaltyAlertsSentDate;
	private UserOrganization userOrganization;
	private String transactionLocationId;
	private Calendar lastFetchedTime;
	private long fetchFreqInMin;
	//sparkbase location OC organisation userid
	private Long orgUserId;
	private String orgUserName;
	private boolean mobileUnique;
    private boolean fetchSeperately;
	
	//private OrganizationStores storeLocation;
	
	public boolean isMobileUnique() {
		   return mobileUnique;
		  }
	 public void setMobileUnique(boolean isMobileUnique) {
		   this.mobileUnique = isMobileUnique;
		  }
	
	
	public boolean isFetchSeperately() {
		return fetchSeperately;
	}
	public void setFetchSeperately(boolean fetchSeperately) {
		this.fetchSeperately = fetchSeperately;
	}
	public String getTransactionLocationId() {
		return transactionLocationId;
	}
	public void setTransactionLocationId(String transactionLocationId) {
		this.transactionLocationId = transactionLocationId;
	}
	
	
	public Calendar getLastFetchedTime() {
		return lastFetchedTime;
	}
	public void setLastFetchedTime(Calendar lastFetchedTime) {
		this.lastFetchedTime = lastFetchedTime;
	}
	public long getFetchFreqInMin() {
		return fetchFreqInMin;
	}
	public void setFetchFreqInMin(long fetchFreqInMin) {
		this.fetchFreqInMin = fetchFreqInMin;
	}
	
	/*public OrganizationStores getStoreLocation() {
		return storeLocation;
	}
	public void setStoreLocation(OrganizationStores storeLocation) {
		this.storeLocation = storeLocation;
	}*/
	public Long getSparkBaseLocationDetails_id() {
		return sparkBaseLocationDetails_id;
	}
	public void setSparkBaseLocationDetails_id(Long sparkBaseLocationDetails_id) {
		this.sparkBaseLocationDetails_id = sparkBaseLocationDetails_id;
	}
	public String getBatchId() {
		return batchId;
	}
	public void setBatchId(String batchId) {
		this.batchId = batchId;
	}
	public String getClientId() {
		return clientId;
	}
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getExternalId() {
		return externalId;
	}
	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}
	public String getInitiatorId() {
		return initiatorId;
	}
	public void setInitiatorId(String initiatorId) {
		this.initiatorId = initiatorId;
	}
	public String getInitiatorPassword() {
		return initiatorPassword;
	}
	public void setInitiatorPassword(String initiatorPassword) {
		this.initiatorPassword = initiatorPassword;
	}
	public String getInitiatorType() {
		return initiatorType;
	}
	public void setInitiatorType(String initiatorType) {
		this.initiatorType = initiatorType;
	}
	public String getIntegrationPassword() {
		return integrationPassword;
	}
	public void setIntegrationPassword(String integrationPassword) {
		this.integrationPassword = integrationPassword;
	}
	public String getIntegrationUserName() {
		return integrationUserName;
	}
	public void setIntegrationUserName(String integrationUserName) {
		this.integrationUserName = integrationUserName;
	}
	public String getLocaleId() {
		return localeId;
	}
	public void setLocaleId(String localeId) {
		this.localeId = localeId;
	}
	public String getLocationId() {
		return locationId;
	}
	public void setLocationId(String locationId) {
		this.locationId = locationId;
	}
	public String getSystemId() {
		return systemId;
	}
	public void setSystemId(String systemId) {
		this.systemId = systemId;
	}
	public String getTerminalId() {
		return terminalId;
	}
	public void setTerminalId(String terminalId) {
		this.terminalId = terminalId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	public Calendar getCreatedDate() {
		return createdDate;
	}
	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}
	public UserOrganization getUserOrganization() {
		return userOrganization;
	}
	public void setUserOrganization(UserOrganization userOrganization) {
		this.userOrganization = userOrganization;
	}
	public boolean isEnableAlerts() {
		return enableAlerts;
	}
	public void setEnableAlerts(boolean enableAlerts) {
		this.enableAlerts = enableAlerts;
	}
	public boolean isEmailAlerts() {
		return emailAlerts;
	}
	public void setEmailAlerts(boolean emailAlerts) {
		this.emailAlerts = emailAlerts;
	}
	
	public boolean isSmsAlerts() {
		return smsAlerts;
	}
	public void setSmsAlerts(boolean smsAlerts) {
		this.smsAlerts = smsAlerts;
	}
	public String getCountType() {
		return countType;
	}
	public void setCountType(String countType) {
		this.countType = countType;
	}
	public String getCountValue() {
		return countValue;
	}
	public void setCountValue(String countValue) {
		this.countValue = countValue;
	}
	public Calendar getLoyaltyAlertsSentDate() {
		return loyaltyAlertsSentDate;
	}
	public void setLoyaltyAlertsSentDate(Calendar loyaltyAlertsSentDate) {
		this.loyaltyAlertsSentDate = loyaltyAlertsSentDate;
	}
	public Long getOrgUserId() {
		return orgUserId;
	}
	public void setOrgUserId(Long orgUserId) {
		this.orgUserId = orgUserId;
	}
	public String getOrgUserName() {
		return orgUserName;
	}
	public void setOrgUserName(String orgUserName) {
		this.orgUserName = orgUserName;
	}
	
}
