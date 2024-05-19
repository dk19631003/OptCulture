package org.mq.loyality.utils;


import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.loyality.common.hbmbean.ContactsLoyalty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;


public class MyAuthenticationToken extends UsernamePasswordAuthenticationToken{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private ContactsLoyalty myUser = null;
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
	public MyAuthenticationToken(Object principal, Object credentials, List<GrantedAuthority> authorities, ContactsLoyalty myUser){
        super(principal, credentials, authorities);
        this.myUser = myUser;   
    }
	
	// ----------------------------------- CONSTRUCTOR
	public MyAuthenticationToken(ContactsLoyalty principal, List<GrantedAuthority> authorities){
		super(principal, authorities);
		
		logger.info("Authorities are=============>"+authorities);
	
		
		
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((myUser == null) ? 0 : myUser.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		MyAuthenticationToken other = (MyAuthenticationToken) obj;
		if (myUser == null) {
			if (other.myUser != null)
				return false;
		} else if (!myUser.equals(other.myUser))
			return false;
		return true;
	}

	public ContactsLoyalty getMyUser() {
		return myUser;
	}
	public void setMyUser(ContactsLoyalty myUser) {
		this.myUser = myUser;
	}
			
}
