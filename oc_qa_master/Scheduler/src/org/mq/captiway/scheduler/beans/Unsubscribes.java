package org.mq.captiway.scheduler.beans;
// Generated 30 Nov, 2009 6:24:52 PM by Hibernate Tools 3.2.0.CR1


import java.util.Date;

/**
 * Unsubscribes generated by hbm2java
 */
public class Unsubscribes {


     private Long unsubscribeId;
     private String reason;
     private Date unsubscribedDate;
     private String emailId;
     private short unsubcategoriesWeight;
     private Long userId;

    public Unsubscribes() {
    }

    public Long getUnsubscribeId() {
        return this.unsubscribeId;
    }
    
    public void setUnsubscribeId(Long unsubscribeId) {
        this.unsubscribeId = unsubscribeId;
    }

    public String getReason() {
        return this.reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
    public Date getUnsubscribedDate() {
        return this.unsubscribedDate;
    }
    
    public void setUnsubscribedDate(Date unsubscribedDate) {
        this.unsubscribedDate = unsubscribedDate;
    }
    
    public Long getuserId() {
        return this.userId;
    }
    
    public void setUserId(Long userId) {
        this.userId = userId;
    }

	public String getEmailId() {
		return emailId;
	}

	public void setEmailId(String emailId) {
		this.emailId = emailId;
	}

	public short getUnsubcategoriesWeight() {
		return unsubcategoriesWeight;
	}

	public void setUnsubcategoriesWeight(short unsubcategoriesWeight) {
		this.unsubcategoriesWeight = unsubcategoriesWeight;
	}

	

}


