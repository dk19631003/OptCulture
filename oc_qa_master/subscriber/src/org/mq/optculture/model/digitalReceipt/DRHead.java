package org.mq.optculture.model.digitalReceipt;

public class DRHead {
	
	private User user;
	
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	private String enrollCustomer;
	private String isLoyaltyCustomer;
	private String emailReceipt;
	private String printReceipt;
	private String requestSource;
	private String requestFormat;
	private String requestEndPoint;
	private String requestId;
	private String requestDate;
	private String requestType;
	private String receiptType;
	
	public String getEnrollCustomer() {
		return enrollCustomer;
	}

	public void setEnrollCustomer(String enrollCustomer) {
		this.enrollCustomer = enrollCustomer;
	}

	public String getIsLoyaltyCustomer() {
		return isLoyaltyCustomer;
	}

	public void setIsLoyaltyCustomer(String isLoyaltyCustomer) {
		this.isLoyaltyCustomer = isLoyaltyCustomer;
	}

	public String getEmailReceipt() {
		return emailReceipt;
	}

	public void setEmailReceipt(String emailReceipt) {
		this.emailReceipt = emailReceipt;
	}

	public String getPrintReceipt() {
		return printReceipt;
	}

	public void setPrintReceipt(String printReceipt) {
		this.printReceipt = printReceipt;
	}

	public String getRequestSource() {
		return requestSource;
	}

	public void setRequestSource(String requestSource) {
		this.requestSource = requestSource;
	}

	public String getRequestFormat() {
		return requestFormat;
	}

	public void setRequestFormat(String requestFormat) {
		this.requestFormat = requestFormat;
	}

	public String getRequestEndPoint() {
		return requestEndPoint;
	}

	public void setRequestEndPoint(String requestEndPoint) {
		this.requestEndPoint = requestEndPoint;
	}

	//private String token;
	/*public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}*/
	public class User{
		
		private String userName;
		//private String organisation;
		private String organizationId;
		private String token;
		
		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}

		/*public String getOrganisation() {
			return organisation;
		}

		public void setOrganisation(String organisation) {
			this.organisation = organisation;
		}*/

		public String getToken() {
			return token;
		}

		public String getOrganizationId() {
			return organizationId;
		}

		public void setOrganizationId(String organizationId) {
			this.organizationId = organizationId;
		}

		public void setToken(String token) {
			this.token = token;
		}
	}

	public String getRequestId() {
		return requestId;
	}

	public void setRequestId(String requestId) {
		this.requestId = requestId;
	}

	public String getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(String requestDate) {
		this.requestDate = requestDate;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}

	public String getReceiptType() {
		return receiptType;
	}

	public void setReceiptType(String receiptType) {
		this.receiptType = receiptType;
	}
	
}
