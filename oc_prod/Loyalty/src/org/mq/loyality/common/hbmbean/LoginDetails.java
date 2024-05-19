package org.mq.loyality.common.hbmbean;

import java.util.Calendar;

public class LoginDetails {
	
	private String userId;
	private String sessionId;
	private String clientIp;
	private String reffUrl;
	private Calendar modDate;
	private Long orgId;
	private Long loyaltyId;
	private Calendar loginDate;
	private Long loginId;
	
	
	public Calendar getModDate() {
		return modDate;
	}
	public void setModDate(Calendar modDate) {
		this.modDate = modDate;
	}
	public Calendar getLoginDate() {
		return loginDate;
	}
	public void setLoginDate(Calendar loginDate) {
		this.loginDate = loginDate;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getClientIp() {
		return clientIp;
	}
	public void setClientIp(String clientIp) {
		this.clientIp = clientIp;
	}
	public String getReffUrl() {
		return reffUrl;
	}
	public void setReffUrl(String reffUrl) {
		this.reffUrl = reffUrl;
	}
	
	public Long getOrgId() {
		return orgId;
	}
	public void setOrgId(Long orgId) {
		this.orgId = orgId;
	}
	public Long getLoyaltyId() {
		return loyaltyId;
	}
	public void setLoyaltyId(Long loyaltyId) {
		this.loyaltyId = loyaltyId;
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((clientIp == null) ? 0 : clientIp.hashCode());
		result = prime * result
				+ ((loginDate == null) ? 0 : loginDate.hashCode());
		result = prime * result
				+ ((loyaltyId == null) ? 0 : loyaltyId.hashCode());
		result = prime * result + ((modDate == null) ? 0 : modDate.hashCode());
		result = prime * result + ((orgId == null) ? 0 : orgId.hashCode());
		result = prime * result + ((reffUrl == null) ? 0 : reffUrl.hashCode());
		result = prime * result
				+ ((sessionId == null) ? 0 : sessionId.hashCode());
		result = prime * result + ((userId == null) ? 0 : userId.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		LoginDetails other = (LoginDetails) obj;
		if (clientIp == null) {
			if (other.clientIp != null)
				return false;
		} else if (!clientIp.equals(other.clientIp))
			return false;
		if (loginDate == null) {
			if (other.loginDate != null)
				return false;
		} else if (!loginDate.equals(other.loginDate))
			return false;
		if (loyaltyId == null) {
			if (other.loyaltyId != null)
				return false;
		} else if (!loyaltyId.equals(other.loyaltyId))
			return false;
		if (modDate == null) {
			if (other.modDate != null)
				return false;
		} else if (!modDate.equals(other.modDate))
			return false;
		if (orgId == null) {
			if (other.orgId != null)
				return false;
		} else if (!orgId.equals(other.orgId))
			return false;
		if (reffUrl == null) {
			if (other.reffUrl != null)
				return false;
		} else if (!reffUrl.equals(other.reffUrl))
			return false;
		if (sessionId == null) {
			if (other.sessionId != null)
				return false;
		} else if (!sessionId.equals(other.sessionId))
			return false;
		if (userId == null) {
			if (other.userId != null)
				return false;
		} else if (!userId.equals(other.userId))
			return false;
		return true;
	}
	public Long getLoginId() {
		return loginId;
	}
	public void setLoginId(Long loginId) {
		this.loginId = loginId;
	}
	
	
	
	
	
	
	

}
