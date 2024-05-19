package org.mq.captiway.scheduler.beans;

public class GoogleAnalyticsVisitsDownloads implements java.io.Serializable {
	
	
	private Long id;
	private String emailName;
	private String url;
	private String action;
	private String sourceOfVisit;
	private String status;
	private Users user;
	
	
	
	public GoogleAnalyticsVisitsDownloads(){}
	public GoogleAnalyticsVisitsDownloads(Users userId) {
		
		this.user = userId;
	}
	
	public GoogleAnalyticsVisitsDownloads(String emailName,String url,String action,
			String sourceOfVisit,String status,Users userId){
		this.emailName=emailName;
		this.url=url;
		this.action=action;
		this.sourceOfVisit=sourceOfVisit;
		this.status=status;
		this.user=userId;
	}
	
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getEmailName() {
		return emailName;
	}

	public void setEmailName(String emailName) {
		this.emailName = emailName;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	public String getSourceOfVisit() {
		return sourceOfVisit;
	}

	public void setSourceOfVisit(String sourceOfVisit) {
		this.sourceOfVisit = sourceOfVisit;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

}
