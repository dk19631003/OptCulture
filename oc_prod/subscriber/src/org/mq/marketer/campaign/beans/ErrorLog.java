package org.mq.marketer.campaign.beans;

import java.util.Calendar;

// Generated 30 Nov, 2009 6:24:52 PM by Hibernate Tools 3.2.0.CR1



/**
 * ErrorLog generated by hbm2java
 */
public class ErrorLog  implements java.io.Serializable {


     private Long refId;
     private String message;
     private String source;
     private Calendar date;
     private Users users;

    public ErrorLog() {
    }

	
    public ErrorLog(String message, Users users) {
        this.message = message;
        this.users = users;
    }
    public ErrorLog(String message, String source, Calendar date, Users users) {
       this.message = message;
       this.source = source;
       this.date = date;
       this.users = users;
    }
   
    public Long getRefId() {
        return this.refId;
    }
    
    public void setRefId(Long refId) {
        this.refId = refId;
    }
    public String getMessage() {
        return this.message;
    }
    
    public void setMessage(String message) {
        this.message = message;
    }
    public String getSource() {
        return this.source;
    }
    
    public void setSource(String source) {
        this.source = source;
    }
    public Calendar getDate() {
        return this.date;
    }
    
    public void setDate(Calendar date) {
        this.date = date;
    }
    public Users getUsers() {
        return this.users;
    }
    
    public void setUsers(Users users) {
        this.users = users;
    }




}


