package org.mq.captiway.scheduler.beans;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;





public class UsersDomains   implements java.io.Serializable, Comparable<UsersDomains> {

	private static final long serialVersionUID = 1L;
	
	private Long domainId;
    private String externalId;
    private String domainName;
    private Users domainManagerId;
	private Calendar createdDate;

	private UserOrganization userOrganization;

	public UserOrganization getUserOrganization() {
		return userOrganization;
	}


	public void setUserOrganization(UserOrganization userOrganization) {
		this.userOrganization = userOrganization;
	}

	//added for sharing
	 private Set<MailingList> mailingLists= new HashSet<MailingList>(0);
	 private Set<SegmentRules> segments= new HashSet<SegmentRules>(0);
	
	

	public Set<SegmentRules> getSegments() {
		return segments;
	}


	public void setSegments(Set<SegmentRules> segments) {
		this.segments = segments;
	}


	public Set<MailingList> getMailingLists() {
		return mailingLists;
	}

	public void setMailingLists(Set<MailingList> mailingLists) {
		this.mailingLists = mailingLists;
	}
	
	
	
	
	public UsersDomains() {
    }
	
	  	
	public Long getDomainId() {
		return domainId;
	}


	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}


	public String getExternalId() {
		return externalId;
	}


	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}


	public String getDomainName() {
		return domainName;
	}


	public void setDomainName(String domainName) {
		this.domainName = domainName;
	}


	public Users getDomainManagerId() {
		return domainManagerId;
	}


	public void setDomainManagerId(Users domainManagerId) {
		this.domainManagerId = domainManagerId;
	}


	public Calendar getCreatedDate() {
		return createdDate;
	}


	public void setCreatedDate(Calendar createdDate) {
		this.createdDate = createdDate;
	}


	@Override
	public int compareTo(UsersDomains user) {
		return user.getDomainName().compareTo(this.domainName);
	}

}
