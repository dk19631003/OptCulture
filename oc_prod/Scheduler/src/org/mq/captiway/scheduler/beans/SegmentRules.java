package org.mq.captiway.scheduler.beans;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import org.mq.captiway.scheduler.beans.MailingList;

public class SegmentRules {

	
	private Long segRuleId;
	private String segRuleName;
	private String description;
	//private MailingList mailingList;
	private Long listId;
	private String segRule;
	private String emailSegQuery;
	private String mobileSegQuery;
	

	private String totSegQuery;
	

	private String customerIdSegQuery;
	private String phoneSegQuery;
	private String segRuleToView;
	private Long userId;
	
	private Calendar createdDate;
	private Calendar modifiedDate;
	private Calendar lastRefreshedOn;
	private long size=0;
	private long totSize = 0;
	

	private long totMobileSize = 0;
	
	
private String segmentMlistIdsStr;
	

	public String getSegmentMlistIdsStr() {
		return segmentMlistIdsStr;
	}

	public void setSegmentMlistIdsStr(String segmentMlistIdsStr) {
		this.segmentMlistIdsStr = segmentMlistIdsStr;
	}
	
	
	//added for sharing
	 private Set<UsersDomains> sharedToDomain = new HashSet<UsersDomains>(0);
	
	 public Set<UsersDomains> getSharedToDomain() {
		return sharedToDomain;
	 }
	
	 public void setSharedToDomain(Set<UsersDomains> sharedToDomain) {
		this.sharedToDomain = sharedToDomain;
	 }
	
	public SegmentRules(){}
	
	/**
	 * partial constructor
	 * @param segRuleName
	 * @param description
	 * @param mailingList
	 * @param segRule
	 * @param segRuleToView
	 * @param userId
	 */
	public SegmentRules(String segRuleName, String description, Long listId,
			String segRule, String emailSegQuery, String totSegQuery, String mobileSegQuery, String segRuleToView, Long userId, Calendar createdDate,
			Calendar modifiedDate, long size, long totSize, long totMobileSize) {
		
		this.segRuleName = segRuleName;
		this.description = description;
		this.listId = listId;
		this.segRule = segRule;
		this.emailSegQuery = emailSegQuery;
		this.totSegQuery = totSegQuery;
		this.mobileSegQuery = mobileSegQuery; 	
		this.segRuleToView = segRuleToView;
		this.userId = userId;
		this.createdDate = createdDate;
		this.modifiedDate = modifiedDate;
		this.size = size;
		this.totSize = totSize;
		this.totMobileSize = totMobileSize;
	}
	
	
	
	
	
	
	public Long getSegRuleId() {
		return segRuleId;
	}
	public void setSegRuleId(Long segRuleId) {
		this.segRuleId = segRuleId;
	}
	public String getSegRuleName() {
		return segRuleName;
	}
	public void setSegRuleName(String segRuleName) {
		this.segRuleName = segRuleName;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	/*public MailingList getMailingList() {
		return mailingList;
	}
	public void setMailingList(MailingList mailingList) {
		this.mailingList = mailingList;
	}*/
	
	public Long getListId() {
		return listId;
	}

	public void setListId(Long listId) {
		this.listId = listId;
	}
	
	public String getSegRule() {
		return segRule;
	}
	public void setSegRule(String segRule) {
		this.segRule = segRule;
	}
	

	public String getSegRuleToView() {
		return segRuleToView;
	}
	public void setSegRuleToView(String segRuleToView) {
		this.segRuleToView = segRuleToView;
	}
	public Long getUserId() {
		return userId;
	}
	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public Calendar getLastRefreshedOn() {
		return lastRefreshedOn;
	}

	public void setLastRefreshedOn(Calendar lastRefreshedOn) {
		this.lastRefreshedOn = lastRefreshedOn;
	}

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}

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
	
	public String getEmailSegQuery() {
		return emailSegQuery;
	}

	public void setEmailSegQuery(String emailSegQuery) {
		this.emailSegQuery = emailSegQuery;
	}

	public String getCustomerIdSegQuery() {
		return customerIdSegQuery;
	}

	public void setCustomerIdSegQuery(String customerIdSegQuery) {
		this.customerIdSegQuery = customerIdSegQuery;
	}

	public String getPhoneSegQuery() {
		return phoneSegQuery;
	}

	public void setPhoneSegQuery(String phoneSegQuery) {
		this.phoneSegQuery = phoneSegQuery;
	}
	
	
	
	public long getTotSize() {
		return totSize;
	}

	public void setTotSize(long totSize) {
		this.totSize = totSize;
	}

	public long getTotMobileSize() {
		return totMobileSize;
	}

	public void setTotMobileSize(long totMobileSize) {
		this.totMobileSize = totMobileSize;
	}
	
	public String getMobileSegQuery() {
		return mobileSegQuery;
	}

	public void setMobileSegQuery(String mobileSegQuery) {
		this.mobileSegQuery = mobileSegQuery;
	}

	public String getTotSegQuery() {
		return totSegQuery;
	}

	public void setTotSegQuery(String totSegQuery) {
		this.totSegQuery = totSegQuery;
	}
	
	
	
}
