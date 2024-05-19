package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.OrgSMSkeywords;
import org.mq.captiway.scheduler.beans.SMSCampReportLists;
import org.mq.captiway.scheduler.beans.SMSSettings;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class SMSSettingsDao extends AbstractSpringDao{

	
public SMSSettingsDao() {}
	
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	 
	
	private JdbcTemplate jdbcTemplate;
	 
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	
	/*public void saveOrUpdate(SMSSettings smsSettings) {
        super.saveOrUpdate(smsSettings);
    }*/

	/*public void delete(SMSSettings smsSettings) {
    super.delete(smsSettings);
}*/
	
	/**
	 * method servers the purpose of fetching the settings of this keyword
	 * @param keyword
	 * @return
	 */
public SMSSettings findByKeyword(String keyword) {
		
		
		List<SMSSettings> keywordsList = null;
				
				String qry =  "FROM SMSSettings WHERE keyword='"+keyword+"' OR optoutKeyword='"+keyword+"' OR helpKeyword='"+keyword+"'";
				
				keywordsList = getHibernateTemplate().find(qry);
				
				if(keywordsList != null && keywordsList.size() > 0) {
					
					return (SMSSettings)keywordsList.get(0);
					
				}else {
					
					return null;
				}
				
			
			
		}
	
public List<SMSSettings> findByUser(Long userId) {

	String qry = " FROM SMSSettings WHERE userId="+userId.longValue();

	List<SMSSettings> list = getHibernateTemplate().find(qry);

	if(list != null && list.size() > 0) {

		return list;

	}else{

		return null;
	}




}


/**
 * method returns the user's organization sms settings(if any) 
 * @param orgId
 * @return
 */
public SMSSettings findByUser(Long userId, String type) {
	
	String qry = " FROM SMSSettings WHERE userId="+userId.longValue()+" AND type='"+type+"'";
	
	List<SMSSettings> list = getHibernateTemplate().find(qry);
	
	if(list != null && list.size() > 0) {
		
		return list.get(0);
		
	}else{
		
		return null;
	}
	
	
	
	
}
/**
 * method returns the user's organization sms settings(if any) 
 * @param orgId
 * @return
 */
public SMSSettings findByOrg(Long orgId, String type) {
	
	String qry = " FROM SMSSettings WHERE orgId="+orgId.longValue()+" AND type='"+type+"'";
	
	List<SMSSettings> list = getHibernateTemplate().find(qry);
	
	if(list != null && list.size() > 0) {
		
		return list.get(0);
		
	}else{
		
		return null;
	}
	
	
	
	
}

public List<SMSSettings> findAllByReceivedNumber(String receivingNumber) {
	
	try {
		String qry = "FROM SMSSettings WHERE shortCode='"+receivingNumber+"'";
		
		logger.debug("SMSSettings ....."+qry);
		List<SMSSettings> retList = executeQuery(qry);
		
		if(retList == null || retList.size() <= 0) {
			
			return null;
		}else{
			
			return retList;
		}
	} catch (Exception e) {
		// TODO Auto-generated catch block
		return null;
	}
	
}
public SMSSettings findByMissedCallNumber(String number) {
	

	
	List<SMSSettings> SMSSettingsList = null;
			
		String qry = " FROM SMSSettings WHERE optinMissedCalNumber like '%"+number+"'";
				
		SMSSettingsList = executeQuery(qry);
		
		if(SMSSettingsList != null && SMSSettingsList.size() > 0) {
			
			return SMSSettingsList.get(0);
			
		}else {
			
			return null;
		}
			
			
			

	
	
}//findByMissedCallNumber

}
