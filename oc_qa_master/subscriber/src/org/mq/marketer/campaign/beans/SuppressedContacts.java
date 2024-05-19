package org.mq.marketer.campaign.beans;

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
     private Calendar suppressedtime;
     private String reason;
     private String source; // changes 2.5.3.0
 	
 	public String getReason() {
		return reason;
	}



	public void setReason(String reason) {
		this.reason = reason;
	}



	public Calendar getSuppressedtime() {
 		return suppressedtime;
 	}



 	public void setSuppressedtime(Calendar suppressedtime) {
 		this.suppressedtime = suppressedtime;
 	}
 	
 	
     
    public SuppressedContacts() {
    }

    public SuppressedContacts(String email) {
       this.email = email;
    }
    
    public SuppressedContacts(Users user, String email, String type) {
    	this.email = email;
    	this.user = user;
    	this.type = type;
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

// change start 2.5.3.0

	public String getSource() {
		return source;
	}



	public void setSource(String source) {
		this.source = source;
	}

	// change end 2.5.3.0
}

