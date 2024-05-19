package org.mq.marketer.campaign.dao;

import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.OrgSMSkeywords;
import org.mq.marketer.campaign.beans.SMSSettings;
import org.mq.marketer.campaign.beans.Users;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.utils.OCConstants;
import org.springframework.jdbc.core.JdbcTemplate;

public class SMSSettingsDaoForDML extends AbstractSpringDaoForDML{

	
public SMSSettingsDaoForDML() {}
	
private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	 
	
	private JdbcTemplate jdbcTemplate;
	 
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	
	public void saveOrUpdate(SMSSettings smsSettings) {
        super.saveOrUpdate(smsSettings);
    }

	public void delete(SMSSettings smsSettings) {
    super.delete(smsSettings);
}
	
	
	// user defined delete
	
	public void deleteBy(Long id) throws Exception {
		
		String qry ="DELETE FROM SMSSettings WHERE setupId="+id.longValue() ;
		executeUpdate(qry);
	}
	
	/**
	 * method servers the purpose of fetching the settings of this keyword
	 * @param keyword
	 * @return
	 */
/*	public Object findByKeyword(String value, String shortCode) {
			
		value = StringEscapeUtils.escapeSql(value);
		List<Object[]> keywordsList = null;
				
			String qry = " FROM SMSSettings s, OrgSMSkeywords o WHERE s."+Constants.SMS_KEYWORD_SETTINGS_TYPE_OPT_IN+"='"+value+"' OR " +
							" s."+Constants.SMS_KEYWORD_SETTINGS_TYPE_HELP+"='"+value+"' OR " +
									" s."+Constants.SMS_KEYWORD_SETTINGS_TYPE_OPT_OUT+"='"+value+"'  OR o.keyword='"+value+"'";
				
		String qry = " FROM SMSSettings s, OrgSMSkeywords o WHERE (s.keyword='"+value+"' AND s.shortCode='"+shortCode+"') OR (" +
					  " o.keyword='"+value+"' AND o.shortCode='"+shortCode+"')";
		
			keywordsList = executeQuery(qry);
			
			if(keywordsList != null && keywordsList.size() > 0) {
				
				return keywordsList.get(0)[0];
				
			}else {
				
				return null;
			}
				
				
				
	}
	
	public List<SMSSettings> findAllByReceivedNumber(String receivingNumber) {
		
		try {
			String qry = "FROM SMSSettings WHERE shortCode='"+receivingNumber+"'";
			
			
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
	
	
	*//**
	 * method returns the user's organization sms settings(if any) 
	 * @param orgId
	 * @return
	 *//*
	public SMSSettings findByOrg(Long orgId) {
		
		String qry = " FROM SMSSettings WHERE orgId="+orgId.longValue();
		
		List<SMSSettings> list = getHibernateTemplate().find(qry);
		
		if(list != null && list.size() > 0) {
			
			return list.get(0);
			
		}else{
			
			return null;
		}
		
		
		
		
	}
	
	public SMSSettings findByOrg(Long orgId, String type) {
			
			String qry = " FROM SMSSettings WHERE orgId="+orgId.longValue()+" AND type='"+type+"'";
			
			List<SMSSettings> list = getHibernateTemplate().find(qry);
			
			if(list != null && list.size() > 0) {
				
				return list.get(0);
				
			}else{
				
				return null;
			}
			
			
			
			
		}
	public List<SMSSettings> findAllByUserOrg(Long orgId) throws Exception {
		
		String qry = " FROM SMSSettings WHERE orgId="+orgId.longValue();
		
		List<SMSSettings> list = getHibernateTemplate().find(qry);
		
		if(list != null && list.size() > 0) {
			
			return list;
			
		}else{
			
			return null;
		}
		
		
		
		
	}
	
	*//**
	 * method returns the user's organization sms settings(if any) 
	 * @param orgId
	 * @return
	 *//*
	public List<SMSSettings> findByUser(Long userId) {
		
		String qry = " FROM SMSSettings WHERE userId="+userId.longValue();
		
		List<SMSSettings> list = getHibernateTemplate().find(qry);
		
		if(list != null && list.size() > 0) {
			
			return list;
			
		}else{
			
			return null;
		}
		
		
		
		
	}
	
  public SMSSettings findHeaderbyUser(Long userId) {
		
		String qry = " FROM SMSSettings WHERE userId="+userId.longValue();
		
       List<SMSSettings> list = getHibernateTemplate().find(qry);
		
		if(list != null && list.size() > 0) {
			
			return list.get(0);
			
		}else{
			
			return null;
		}
		
		}

	
	
	*//**
	 * method returns the user's organization sms settings(if any) 
	 * @param orgId
	 * @return
	 *//*
	public SMSSettings findByUser(Long userId, String type) {
		
		String qry = " FROM SMSSettings WHERE userId="+userId.longValue()+" AND type='"+type+"'";
		
		List<SMSSettings> list = getHibernateTemplate().find(qry);
		
		if(list != null && list.size() > 0) {
			
			return list.get(0);
			
		}else{
			
			return null;
		}
		
		
		
		
	}
	
	*//**
	 * this method searches for the given header
	 * @param header
	 * @return
	 *//*
	public SMSSettings findByHeader(String header, String shortCode) {
		//if(header.contains("'")) header = header.replace("'", "\\'");
		
		header = StringEscapeUtils.escapeSql(header);
		
		String qry = "FROM SMSSettings WHERE messageHeader='"+header+"' AND shortCode='"+shortCode+"'";
		
		List<SMSSettings> list = getHibernateTemplate().find(qry);
		
		if(list != null && list.size() > 0) {
			
			return list.get(0);
			
		}else{
			
			return null;
		}
		
	}

	public List<Object[]> findAllkeyWordResponses(Long userId, Long orgId, String type) {
		
	
		//what about the old keyword's responses?
		String qry=	" SELECT  k.keyword,  COUNT( i.inboundMsgId) FROM SMSSettings k, ClickaTellSMSInbound i WHERE " +
					"  i.orgId IS NOT NULL AND i.orgId="+orgId.longValue()+" AND k.type='"+type+"' AND k.userId="+userId.longValue()+" AND i.text IS NOT NULL AND i.text!='' AND k.orgId=i.orgId" +
					" AND k.keyword=i.usedKeyWords ";

		List<Object[]> retList = executeQuery(qry);
		if(retList != null && retList.size() > 0) return retList;
		else return null;
		
		
	}//findAllkeyWordResponses
*/	
/*	public List<Object[]> findAllOptOutkeyWordResponses(Long userId, Long orgId) {
		
		
		//what about the old keyword's responses?
		String qry=	" SELECT  k.optoutKeyword,  COUNT( i.inboundMsgId) FROM SMSSettings k, ClickaTellSMSInbound i WHERE " +
					"  i.orgId IS NOT NULL AND i.orgId="+orgId.longValue()+" AND k.userId="+userId.longValue()+" AND i.text IS NOT NULL AND i.text!='' AND k.orgId=i.orgId" +
					" AND k.optoutKeyword=i.usedKeyWords ";
		List<Object[]> retList = executeQuery(qry);
		if(retList != null && retList.size() > 0) return retList;
		else return null;
		
		
	}//findAllkeyWordResponses
	
public List<Object[]> findAllHelpkeyWordResponses(Long userId, Long orgId) {
		
		
		//what about the old keyword's responses?
		String qry=	" SELECT  k.helpKeyword,  COUNT( i.inboundMsgId) FROM SMSSettings k, ClickaTellSMSInbound i WHERE " +
					"  i.orgId IS NOT NULL AND i.orgId="+orgId.longValue()+" AND k.userId="+userId.longValue()+" AND i.text IS NOT NULL AND i.text!='' AND k.orgId=i.orgId" +
					" AND k.helpKeyword=i.usedKeyWords ";

		List<Object[]> retList = executeQuery(qry);
		if(retList != null && retList.size() > 0) return retList;
		else return null;
		
		
	}//findAllkeyWordResponses
	*/
	/**
	 *  Added for User organization
	 * @param userId
	 * @param type
	 * @return
	 */
	
	
/*public SMSSettings findByUserOrg(Long orgId, String type) throws Exception {
		
		String qry = " FROM SMSSettings WHERE orgId="+orgId.longValue()+" AND type='"+type+"'";
		
		List<SMSSettings> list = getHibernateTemplate().find(qry);
		
		if(list != null && list.size() > 0) {
			
			return list.get(0);
			
		}else{
			
			return null;
		}
		
		
		
		
	}
*/
}
