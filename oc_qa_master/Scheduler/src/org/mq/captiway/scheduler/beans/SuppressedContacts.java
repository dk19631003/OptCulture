package org.mq.captiway.scheduler.beans;

import java.util.Calendar;
// Generated 30 Nov, 2009 6:24:52 PM by Hibernate Tools 3.2.0.CR1



/**
 * SuppressedContacts generated by hbm2java
 */
public class SuppressedContacts  implements java.io.Serializable {


     private Long id;
     private String email;
     private Users user;
     private String type;
     private String reason;
  	
  	
  	public String getReason() {
 		return reason;
 	}



 	public void setReason(String reason) {
 		this.reason = reason;
 	}
     
    public SuppressedContacts() {
    }

    
    private Calendar suppressedtime;
 	
 	
 	public Calendar getSuppressedtime() {
 		return suppressedtime;
 	}



 	public void setSuppressedtime(Calendar suppressedtime) {
 		this.suppressedtime = suppressedtime;
 	}
    
    public SuppressedContacts(String email) {
       this.email = email;
    }
   
    public Long getId() {
        return this.id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }

	public Users getUser() {
		return user;
	}

	public void setUser(Users user) {
		this.user = user;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}


}


