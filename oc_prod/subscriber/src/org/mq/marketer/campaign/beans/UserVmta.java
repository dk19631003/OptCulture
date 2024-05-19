package org.mq.marketer.campaign.beans;

public class UserVmta implements java.io.Serializable {

	private Long Id;
	private Long vmtaId;
	private Long userId;
	private String emailType;
	private boolean enabled;
	
	public UserVmta() {
		
	}
	public UserVmta(Long vmtaId, Long userId, String emailType, boolean enabled) {
		this.vmtaId = vmtaId;
		this.userId = userId;
		this.emailType = emailType;
		this.enabled = enabled;
	}
	public Long getId() {
		return Id;
	}
	public void setId(Long id) {
		Id = id;
	}
	public Long getVmtaId() {
		return vmtaId;
	}
	public void setVmtaId(Long vmtaId) {
		this.vmtaId = vmtaId;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}
	public String getEmailType() {
		return emailType;
	}
	public void setEmailType(String emailType) {
		this.emailType = emailType;
	}
	public boolean isEnabled() {
		return enabled;
	}
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}
	
}
