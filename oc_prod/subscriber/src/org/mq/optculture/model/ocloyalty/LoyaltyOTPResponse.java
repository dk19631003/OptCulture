package org.mq.optculture.model.ocloyalty;

/**
 * 
 * @author venkata rathnam d
 *
 */
public class LoyaltyOTPResponse {

	private ResponseHeader header;
	private String otpCode;
	private Status status;
	private String sessionID;
	

	
	public LoyaltyOTPResponse() {
	}
	public ResponseHeader getHeader() {
		return header;
	}
	public void setHeader(ResponseHeader header) {
		this.header = header;
	}
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public String getOtpCode() {
		return otpCode;
	}
	public void setOtpCode(String otpCode) {
		this.otpCode = otpCode;
	}
	public String getSessionID() {
		return sessionID;
	}
	public void setSessionID(String sessionID) {
		this.sessionID = sessionID;
	}
	
	
}
