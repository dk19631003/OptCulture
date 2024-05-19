package org.mq.optculture.model.loyalty;

import org.mq.optculture.model.BaseResponseObject;

public class LoyaltyBaseResponseObject extends BaseResponseObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3739413990646192793L;
	private HeaderInfo headerInfo;
	private EnrollmentInfo enrollmentInfo;
	private CustomerInfo customerInfo;
	private AmountDetails amountDetails;
	private UserDetails userDetails;
	public HeaderInfo getHeaderInfo() {
		return headerInfo;
	}
	public void setHeaderInfo(HeaderInfo headerInfo) {
		this.headerInfo = headerInfo;
	}
	public EnrollmentInfo getEnrollmentInfo() {
		return enrollmentInfo;
	}
	public void setEnrollmentInfo(EnrollmentInfo enrollmentInfo) {
		this.enrollmentInfo = enrollmentInfo;
	}
	public CustomerInfo getCustomerInfo() {
		return customerInfo;
	}
	public void setCustomerInfo(CustomerInfo customerInfo) {
		this.customerInfo = customerInfo;
	}
	public AmountDetails getAmountDetails() {
		return amountDetails;
	}
	public void setAmountDetails(AmountDetails amountDetails) {
		this.amountDetails = amountDetails;
	}
	public UserDetails getUserDetails() {
		return userDetails;
	}
	public void setUserDetails(UserDetails userDetails) {
		this.userDetails = userDetails;
	}
	
}
