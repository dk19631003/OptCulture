package org.mq.marketer.campaign.dao;

import java.util.List;

import org.mq.marketer.campaign.beans.UserSMSGateway;
import org.springframework.jdbc.core.JdbcTemplate;

public class UserSMSGatewayDao extends AbstractSpringDao{
	
	

	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	/*public void saveOrUpdate(UserSMSGateway userSMSGateway) {
		super.saveOrUpdate(userSMSGateway);
	}

	public void delete(UserSMSGateway userSMSGateway) {
		super.delete(userSMSGateway);
	}
	
	
	public void deleteBy(Long userSMSGatewayId) throws Exception{
		
		String qry ="DELETE FROM UserSMSGateway WHERE id="+userSMSGatewayId.longValue();
		
		executeUpdate(qry);
		
	}*/
	
	public UserSMSGateway findByUserId(Long userId, String accountType) {
		
		String qry = "FROM UserSMSGateway WHERE userId="+userId.longValue()+" AND accountType='"+accountType+"'";
		
		List<UserSMSGateway> retList =  executeQuery(qry);
		
		if(retList != null && retList.size() > 0) {
			
			return retList.get(0);
			
		}else{
			
			return null;
		}
		
	}
	
	
	public List<UserSMSGateway> findAllBy(Long userId, String typeStr) throws Exception{
		
		
		String qry = "FROM UserSMSGateway WHERE userId="+userId.longValue()+" AND accountType IN("+typeStr+")";
		
		List<UserSMSGateway> retList =  executeQuery(qry);
		
		if(retList != null && retList.size() > 0) {
			
			return retList;
			
		}else{
			
			return null;
		}
		
	}
	
	public List<UserSMSGateway> findAllByUserId(Long userId) throws Exception{
		
		
		String qry = "FROM UserSMSGateway WHERE userId="+userId.longValue();
		
		List<UserSMSGateway> retList =  executeQuery(qry);
		
		if(retList != null && retList.size() > 0) {
			
			return retList;
			
		}else{
			
			return null;
		}
		
	}
	public List<UserSMSGateway> findAllByGatewayId(Long gatewayId) throws Exception{
		
		
		String qry = "FROM UserSMSGateway WHERE gatewayId="+gatewayId.longValue();
		
		List<UserSMSGateway> retList =  executeQuery(qry);
		
		if(retList != null && retList.size() > 0) {
			
			return retList;
			
		}else{
			
			return null;
		}
		
	}
}
