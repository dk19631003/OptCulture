package org.mq.captiway.scheduler.dao;

import java.util.Collection;

import org.mq.captiway.scheduler.beans.NotSentToContacts;

public class NotSentToContactsDaoForDML extends AbstractSpringDaoForDML {

	public NotSentToContactsDaoForDML() {}
	
	 public void saveOrUpdate(NotSentToContacts notSentToContact) {
        super.saveOrUpdate(notSentToContact);
    }
	    
	 public void saveByCollection(Collection notSentToContactCollection){ //added for EventTrigger
	    	
	    		super.saveOrUpdateAll(notSentToContactCollection);
	    		
	    	
	    } // saveByCollection
	
	
	
	
}//NotSentToContactsDao
