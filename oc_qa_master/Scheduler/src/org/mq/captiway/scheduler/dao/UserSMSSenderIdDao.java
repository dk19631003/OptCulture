package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.mq.captiway.scheduler.beans.UserSMSSenderId;


public class UserSMSSenderIdDao extends AbstractSpringDao {

	
	public List<UserSMSSenderId> findByUserId(Long userId) {
		
		List<UserSMSSenderId> senderIds = null;
		
		senderIds = getHibernateTemplate().find("FROM UserSMSSenderId where userId="+userId.longValue());
		
		
		return senderIds;
		
	}
	
	public List<UserSMSSenderId> findSenderIdBySMSType(Long userId,String smsType){
		List<UserSMSSenderId> senderIds = null;
		
		//senderIds = executeQuery("FROM UserSMSSenderId where userId="+userId.longValue()+" and smsType='"+smsType+"'");
		
		String qry = "FROM UserSMSSenderId where userId="+userId.longValue()+" and smsType='"+smsType+"'";
		
		senderIds = executeQuery(qry);
		
		
		if(senderIds != null && senderIds.size() > 0) return senderIds;
		
		return null;
	}
	
}
