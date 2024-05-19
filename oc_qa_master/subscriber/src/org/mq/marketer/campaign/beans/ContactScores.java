package org.mq.marketer.campaign.beans;

import java.util.Calendar;

public class ContactScores implements java.io.Serializable {


	private Long id;
	private String emailId;
	private String phone;
	private Integer pageVisitedCount;
	private Integer downLoadedCount;
	private Integer sourceOfVisitCount;
	private Integer emailOpenedCount;
	private Integer emailClickedCount;
	private Integer emailNotOpenedCount;
	private Integer emailUnsubscribedCount;
	private Integer formSubmittedCount;
	private Integer formAbondonedCount;
	private Integer formFillRatioCount;
	private Long total;
	private Calendar lastModifiedDate;
	private Users user;
	
	public ContactScores(){}
	
	public ContactScores(Users userId) {
		
		this.user = userId;
	}



	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Integer getPageVisitedCount() {
		return pageVisitedCount;
	}

	public void setPageVisitedCount(Integer pageVisitedCount) {
		this.pageVisitedCount = pageVisitedCount;
	}

	public Integer getDownLoadedCount() {
		return downLoadedCount;
	}

	public void setDownLoadedCount(Integer downLoadedCount) {
		this.downLoadedCount = downLoadedCount;
	}

	public Integer getSourceOfVisitCount() {
		return sourceOfVisitCount;
	}

	public void setSourceOfVisitCount(Integer sourceOfVisitCount) {
		this.sourceOfVisitCount = sourceOfVisitCount;
	}

	public Integer getEmailOpenedCount() {
		return emailOpenedCount;
	}

	public void setEmailOpenedCount(Integer emailOpenedCount) {
		this.emailOpenedCount = emailOpenedCount;
	}

	public Integer getEmailClickedCount() {
		return emailClickedCount;
	}

	public void setEmailClickedCount(Integer emailClickedCount) {
		this.emailClickedCount = emailClickedCount;
	}

	public Integer getEmailNotOpenedCount() {
		return emailNotOpenedCount;
	}

	public void setEmailNotOpenedCount(Integer emailNotOpenedCount) {
		this.emailNotOpenedCount = emailNotOpenedCount;
	}

	public Integer getEmailUnsubscribedCount() {
		return emailUnsubscribedCount;
	}

	public void setEmailUnsubscribedCount(Integer emailUnsubscribedCount) {
		this.emailUnsubscribedCount = emailUnsubscribedCount;
	}

	public Integer getFormSubmittedCount() {
		return formSubmittedCount;
	}

	public void setFormSubmittedCount(Integer formSubmittedCount) {
		this.formSubmittedCount = formSubmittedCount;
	}

	public Integer getFormAbondonedCount() {
		return formAbondonedCount;
	}

	public void setFormAbondonedCount(Integer formAbondonedCount) {
		this.formAbondonedCount = formAbondonedCount;
	}

	public Integer getFormFillRatioCount() {
		return formFillRatioCount;
	}

	public void setFormFillRatioCount(Integer formFillRatioCount) {
		this.formFillRatioCount = formFillRatioCount;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public String getEmailId() {
		return emailId;
	}
	
	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}
	
	public String getPhone() {
		return phone;
	}
	
	public void setPhone(String phone) {
		this.phone = phone;
	}
	
	public Long getTotal() {
		return total;
	}
	
	public void setTotal(Long total) {
		this.total = total;
	}
	
	
	public Calendar getLastModifiedDate() {
		return lastModifiedDate;
	}
	
	public void setLastModifiedDate(Calendar lastModifiedDate) {
		this.lastModifiedDate = lastModifiedDate;
	}
}
