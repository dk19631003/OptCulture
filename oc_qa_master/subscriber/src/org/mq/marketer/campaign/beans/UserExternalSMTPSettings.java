package org.mq.marketer.campaign.beans;

import java.util.Calendar;

/*
 * this class is explicitly defined for send grid api however it is designed to 
 * support any other apis as well for future purpose .  
 */
public class UserExternalSMTPSettings implements java.io.Serializable {


		 private Long id;
	     private String externalSMTP;
	     private String userName;
	     private String password;
	     private String emailId;
	     private Calendar createdDate;
	     private boolean enabled;
	     private Integer emailCount;
	     private Integer usedEmailCount;
	     private Users users;
	     private boolean freeUser;

	    public UserExternalSMTPSettings() {
	    }

	    public UserExternalSMTPSettings(String externalSMTP, String userName, String password, String emailId,
	    		String firstName, String lastName, String companyName, Calendar createdDate, 
	    		String addressOne, String addressTwo, String city, String state, String country,
	    		String pinCode, String phone, boolean enabled) {
	    	
	       this.externalSMTP = externalSMTP;
	       this.userName = userName;
	       this.password = password;
	       this.emailId = emailId;
	       this.createdDate = createdDate;
	       this.enabled = enabled;
	    }
	   
	  
	    public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public String getExternalSMTP() {
			return externalSMTP;
		}

		public void setExternalSMTP(String externalSMTP) {
			this.externalSMTP = externalSMTP;
		}

		public Users getUsers() {
			return users;
		}

		public void setUsers(Users users) {
			this.users = users;
		}

		public String getUserName() {
	        return this.userName;
	    }
	    
	    public void setUserName(String userName) {
	        this.userName = userName;
	    }
	    public String getPassword() {
	        return this.password;
	    }
	    
	    public void setPassword(String password) {
	        this.password = password;
	    }
	    public String getEmailId() {
	        return this.emailId;
	    }
	    
	    public void setEmailId(String emailId) {
	        this.emailId = emailId;
	    }
	 
	    public Calendar getCreatedDate() {
	        return this.createdDate;
	    }
	    
	    public void setCreatedDate(Calendar createdDate) {
	        this.createdDate = createdDate;
	    }
	 
		public boolean isEnabled() {
	        return this.enabled;
	    }
	    
	    public void setEnabled(boolean enabled) {
	        this.enabled = enabled;
	    }
	    
	  
	    public void setEmailCount(Integer emailCount) {
	    	this.emailCount = emailCount;
	    }
	    
	    public Integer getEmailCount() {
	    	return this.emailCount;
	    }
	    
	    public void setUsedEmailCount(Integer usedEmailCount) {
	    	this.usedEmailCount = usedEmailCount;
	    }
	    
	    public Integer getUsedEmailCount() {
	    	return this.usedEmailCount;
	    }

		public boolean isFreeUser() {
			return freeUser;
		}

		public void setFreeUser(boolean freeUser) {
			this.freeUser = freeUser;
		}

	    
	}



