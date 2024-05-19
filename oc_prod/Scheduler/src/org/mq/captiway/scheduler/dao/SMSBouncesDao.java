package org.mq.captiway.scheduler.dao;

import java.util.Collection;
import java.util.List;

import org.mq.captiway.scheduler.beans.Bounces;
import org.mq.captiway.scheduler.beans.SMSBounces;

public class SMSBouncesDao extends AbstractSpringDao{

	
	 /*public void saveOrUpdate(SMSBounces smsBounce) {
	        super.saveOrUpdate(smsBounce);
	    }

	 public void saveByCollection(Collection<SMSBounces> campList) {
			super.saveOrUpdateAll(campList);
		}
	 
	 public int deleteByMobile(Long SmsCrId, String mobile, long cliMsgIdLong) {
		 
		 String qry = "DELETE FROM SMSBounces WHERE crId="+SmsCrId.longValue()+" AND mobile LIKE '%"+mobile+"' AND sentId="+cliMsgIdLong;
		 
		 
		int count = executeUpdate(qry);
		
		/*if(count > 0) {
			executeUpdate("UPDATE SMSCampaignReport SET bounces=(bounces-"+count+") WHERE smsCrId="+SmsCrId.longValue());
		}*/
		
		/*return count;
		 
	 }*/
	
	 public SMSBounces findBymobile(Long crId, String mobile) {
		 
		 String qry = "FROM SMSBounces WHERE crId="+crId+" AND mobile = '"+mobile+"'";
		 
		 List<SMSBounces> list = getHibernateTemplate().find(qry);
		 
		 if(list != null && list.size() > 0) {
			 
			 return list.get(0);
		 }
		 else return null;
	 }
	 
	 
	 
	 
	 
}
