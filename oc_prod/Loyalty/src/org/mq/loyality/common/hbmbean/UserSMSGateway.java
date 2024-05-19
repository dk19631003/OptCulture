package org.mq.loyality.common.hbmbean;

import java.util.Calendar;

/**
 * 
 * @author proumyaa
 *
 */
public class UserSMSGateway {

	private Long id;
	private Long userId;
	private Long orgId;
	private String accountType;
	
	/*private String smsMode;
	private boolean status;
	private String accountDetails;
*/	
	private Calendar createdDate;
	
	private  Long gatewayId;
	private Calendar modifiedDate;
	private Long createdBy;
	private Long modifiedBy;
	private boolean checkSettings;

	public UserSMSGateway() {}

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

	public Long getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(Long createdBy) {
		this.createdBy = createdBy;
	}

	public Long getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(Long modifiedBy) {
		this.modifiedBy = modifiedBy;
	}
	
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getAccountType() {
		return accountType;
	}

	public void setAccountType(String accountType) {
		this.accountType = accountType;
	}

	public Long getOrgId() {
		return orgId;
	}

	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}

	
	
	public boolean isCheckSettings() {
		return checkSettings;
	}

	public void setCheckSettings(boolean checkSettings) {
		this.checkSettings = checkSettings;
	}
	public Long getGatewayId() {
		return gatewayId;
	}

	public void setGatewayId(Long gatewayId) {
		this.gatewayId = gatewayId;
	}
	
}
