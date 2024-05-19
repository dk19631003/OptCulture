package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.UserSMSSenderId;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserSMSSenderIdDao extends AbstractSpringDao {

	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	/* public void saveOrUpdate(UserSMSSenderId userSMSSenderId){
	        super.saveOrUpdate(userSMSSenderId);
	 }
	
	 public void delete(UserSMSSenderId userSMSSenderId) {
			super.delete(userSMSSenderId);
	}
	 
	 
	public void deleteBy(Long userSMSId) throws Exception{
		
		String qry ="DELETE FROM UserSMSSenderId WHERE id="+userSMSId.longValue();
		
		executeUpdate(qry);
		
	}*/
	
	public List<UserSMSSenderId> findByUserId(Long userId) {
		
		List<UserSMSSenderId> senderIds = null;
		
		senderIds = executeQuery("FROM UserSMSSenderId where userId="+userId);
		
		if(senderIds != null && senderIds.size() > 0) return senderIds;
		
		return null;
		
	}
	/*public String findSenderIdBySMSType(Long userId,String smsType){
		List<UserSMSSenderId> senderIdsObj = null;
		
		senderIdsObj = getHibernateTemplate().find("FROM UserSMSSenderId where userId="+userId+" and smsType='"+smsType+"'");
		String senderId = "";
		if(senderIdsObj.size() > 0) senderId = senderIdsObj.get(0).getSenderId();
		return senderId;
	}	*/

	public List<UserSMSSenderId> findSenderIdBySMSType(Long userId,String smsType){
		List<UserSMSSenderId> senderIds = null;
		
		String qry = "FROM UserSMSSenderId where userId="+userId.longValue()+" and smsType='"+smsType+"'";
		senderIds = executeQuery(qry);
		
		
		if(senderIds != null && senderIds.size() > 0) return senderIds;
		
		return null;
	}
	
	
	
}
