package org.mq.marketer.campaign.dao;

import java.util.Collection;
import java.util.List;

import org.mq.marketer.campaign.beans.WABounces;


public class WABouncesDaoForDML extends AbstractSpringDaoForDML{

	
	 public void saveOrUpdate(WABounces waBounce) {
	        super.saveOrUpdate(waBounce);
	    }

	 
	 public int deleteByMobile(Long waCrId, String mobile, long cliMsgIdLong) {
		 
		 String qry = "DELETE FROM WABounces WHERE crId="+waCrId.longValue()+" AND mobile LIKE '%"+mobile+"' AND sentId="+cliMsgIdLong;
		 
		 
		int count = executeUpdate(qry);
		
		return count;
		 
	 }
	
	 
	 
}

