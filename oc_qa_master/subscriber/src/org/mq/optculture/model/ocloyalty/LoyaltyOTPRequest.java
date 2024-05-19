package org.mq.optculture.model.ocloyalty;
/**
 * OC Loyalty program OTP request JSON Object 
 * 
 * @author Venkata Rathnam D 
 *
 */
public class LoyaltyOTPRequest {

	private RequestHeader header;
	private String otpCode; //APP-1976
	private Customer customer;
	private LoyaltyUser user;
	private String issueOnAction;//reset, enroll, redemption,login etc
	
	public LoyaltyOTPRequest() {
	}

	public RequestHeader getHeader() {
		return header;
	}

	public void setHeader(RequestHeader header) {
		this.header = header;
	}

		
	public Customer getCustomer() {
		return customer;
	}

	public void setCustomer(Customer customer) {
		this.customer = customer;
	}

	public LoyaltyUser getUser() {
		return user;
	}

	public void setUser(LoyaltyUser user) {
		this.user = user;
	}

	public String getOtpCode() {
		return otpCode;
	}

	public void setOtpCode(String otpCode) {
		this.otpCode = otpCode;
	}

	
	

	public String getIssueOnAction() {
		return issueOnAction;
	}

	public void setIssueOnAction(String issueOnAction) {
		this.issueOnAction = issueOnAction;
	}

}
