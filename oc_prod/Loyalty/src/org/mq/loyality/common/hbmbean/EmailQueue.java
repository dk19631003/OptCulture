package org.mq.loyality.common.hbmbean;


import java.util.Calendar;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.loyality.utils.Constants;


// Generated 24 Jun, 2009 5:18:38 PM by Hibernate Tools 3.2.0.CR1



/**
 * Campaigns generated by hbm2java
 */
@SuppressWarnings({"unchecked","serial"})
public class EmailQueue  implements java.io.Serializable {

	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
    private long id; 
    private String type;
    private String status;
    private String toEmailId;
    private String message;
    private String subject;
    private Calendar sentDate;
    private Users user;
   
	private String childEmail;
    private String childFirstName;
    private String dateOfBirth;
    private Long contactId; 
    
    private String ccEmailId;
    private Long loyaltyId;
    
    public String getCcEmailId() {
		return ccEmailId;
	}

	public void setCcEmailId(String ccEmailId) {
		this.ccEmailId = ccEmailId;
	}
    
    private CustomTemplates customTemplates;
    
    
    public CustomTemplates getCustomTemplates() {
		return customTemplates;
	}

	public void setCustomTemplates(CustomTemplates customTemplates) {
		this.customTemplates = customTemplates;
	}

    
    public EmailQueue(){
    }
	
    public EmailQueue(String subject,String message, String type, String status,String toEmailId,Calendar sentDate,Users user) {
    	this.subject = subject;
    	this.message = message;
    	this.type = type;
        this.status = status;
        this.toEmailId = toEmailId;
        this.sentDate = sentDate;
        this.user = user;
    }
    
   /* public EmailQueue(Campaigns campaigns, String type, String status, String toEmailId,Calendar sentDate,Users user) {
    	this.campaign = campaigns;
    	this.type = type;
        this.status = status;
        this.toEmailId = toEmailId;
        this.sentDate = sentDate;
        this.user = user;
    }*/
   
    
    /*public EmailQueue(Campaigns campaigns, CustomTemplates customTemplates, String type, String status, String toEmailId,Calendar sentDate,Users user) {
    	this.campaign = campaigns;
    	this.customTemplates = customTemplates;
    	this.type = type;
        this.status = status;
        this.toEmailId = toEmailId;
        this.sentDate = sentDate;
        this.user = user;
    }*/
    
    
    public EmailQueue(CustomTemplates customTemplates, String type, String message,
  		  String status, String toEmailId,Users user,Calendar sentDate, String subject, String childEmail, String childFirstName, String dateOfBirth, Long contactId) {
    	this.subject = subject;
    	this.message = message;
    	this.customTemplates = customTemplates;
    	this.type = type;
    	this.status = status;
    	this.toEmailId = toEmailId;
    	this.user = user;
    	this.sentDate = sentDate;
    	this.childEmail = childEmail;
    	this.dateOfBirth = dateOfBirth;
    	this.childFirstName = childFirstName;
    	this.contactId = contactId;
    	
    }
    
    public EmailQueue(String subject,String message, String type, String status,String toEmailId,Calendar sentDate) {
    logger.info("setting eamil queue::::");
  	  this.subject = subject;
  	  this.message = message;
  	  this.type = type;
  	  this.status = status;
  	  this.toEmailId = toEmailId;
  	  this.sentDate = sentDate;
    }

    
    public EmailQueue(CustomTemplates customTemplates, String type, String message,
    		String status, String toEmailId,Users user,Calendar sentDate, String subject, Long contactId,Long loyaltyId) {
    	this.customTemplates = customTemplates;
    	this.subject = subject;
    	this.message = message;
    	this.type = type;
    	this.status = status;
    	this.toEmailId = toEmailId;
    	this.sentDate = sentDate;
    	this.user = user;
    	this.contactId = contactId;
    	this.loyaltyId = loyaltyId;
    }
  
    
    public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
    public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getToEmailId() {
        return this.toEmailId;
    }
    
    public void setToEmailId(String toEmailId) {
        this.toEmailId = toEmailId;
    }

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Calendar getSentDate() {
		return sentDate;
	}

	public void setSentDate(Calendar sentDate) {
		this.sentDate = sentDate;
	}

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	
	
	
	 public String getChildEmail() {
			return childEmail;
		}

		public void setChildEmail(String childEmail) {
			this.childEmail = childEmail;
		}

		public String getChildFirstName() {
			return childFirstName;
		}

		public void setChildFirstName(String childFirstName) {
			this.childFirstName = childFirstName;
		}

		public String getDateOfBirth() {
			return dateOfBirth;
		}

		public void setDateOfBirth(String dateOfBirth) {
			this.dateOfBirth = dateOfBirth;
		}

		public Long getContactId() {
			return contactId;
		}

		public void setContactId(Long contactId) {
			this.contactId = contactId;
		}

		public Long getLoyaltyId() {
			return loyaltyId;
		}

		public void setLoyaltyId(Long loyaltyId) {
			this.loyaltyId = loyaltyId;
		}
}

