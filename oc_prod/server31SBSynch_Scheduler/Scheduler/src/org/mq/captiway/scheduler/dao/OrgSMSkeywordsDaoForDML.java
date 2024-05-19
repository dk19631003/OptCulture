package org.mq.captiway.scheduler.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.EventTriggerEvents;
import org.mq.captiway.scheduler.beans.OrgSMSkeywords;
import org.mq.captiway.scheduler.beans.Users;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.utils.OCConstants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;


public class OrgSMSkeywordsDaoForDML extends AbstractSpringDaoForDML {

	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private JdbcTemplate jdbcTemplate;
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public OrgSMSkeywordsDaoForDML() {
		
	}
	/*public List<OrgSMSkeywords> findExpKeywordBy(Long orgId) throws Exception{
		
		
		List<OrgSMSkeywords> keywordsList = null;
				
		String qry = "FROM OrgSMSkeywords WHERE orgId="+orgId.longValue()+" AND status='"+Constants.KEYWORD_STATUS_EXPIRED+"'";
		
		keywordsList = executeQuery(qry);
		
		if(keywordsList != null && keywordsList.size() > 0) {
			
			return keywordsList;
			
		}else {
			
			return null;
		}
				
			
			
	}
	
	public List<OrgSMSkeywords> findNonActiveKeywordBy(Long orgId) throws Exception{
		
		
		List<OrgSMSkeywords> keywordsList = null;
				
		String qry = "FROM OrgSMSkeywords WHERE orgId="+orgId.longValue()+" AND "
				+ " DATE(NOW()) < DATE(startFrom) AND status='"+Constants.KEYWORD_STATUS_PENDING+"'";
		
		keywordsList = executeQuery(qry);
		
		if(keywordsList != null && keywordsList.size() > 0) {
			
			return keywordsList;
			
		}else {
			
			return null;
		}
				
			
			
	}
	
	
	
	public Long findorgByKeyword(String keyword) {
		
		
	List<Long> keywordsList = null;
			
			String qry = "SELECT orgId FROM OrgSMSkeywords WHERE keyword='"+keyword+"'";
			
			keywordsList = getHibernateTemplate().find(qry);
			
			if(keywordsList != null && keywordsList.size() > 0) {
				
				return (Long)keywordsList.get(0);
				
			}else {
				
				return null;
			}
			
		
		
	}
	
	
	public OrgSMSkeywords findByKeyword(String keyword) {
		
		
		List<OrgSMSkeywords> keywordsList = null;
				
				String qry = "FROM OrgSMSkeywords WHERE keyword='"+keyword+"'";
				
				keywordsList = getHibernateTemplate().find(qry);
				
				if(keywordsList != null && keywordsList.size() > 0) {
					
					return (OrgSMSkeywords)keywordsList.get(0);
					
				}else {
					
					return null;
				}
				
			
			
		}
	
	public List<OrgSMSkeywords> getUserOrgSMSKeyWords(Long orgId) {
		
		List<OrgSMSkeywords> keywordsList = null;
		
		String qry = "FROM OrgSMSkeywords WHERE orgId="+orgId.longValue()+" AND validUpto >= now() ";
		
		keywordsList = getHibernateTemplate().find(qry);
		
		return keywordsList;
		
		
		
	}
	
	public List<String> findKeywords() {
		
		
		
		
		return null;
	}
	
	public List<OrgSMSkeywords> findAllByReceivedNumber(String receivingNumber) {
		
		try {
			String qry = "FROM OrgSMSkeywords WHERE shortCode='"+receivingNumber+"'";
			
			logger.debug("OrgSMSkeywords..."+qry);
			List<OrgSMSkeywords> retList = executeQuery(qry);
			
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
	
	public int getExpKeywordCount() {
		
		try {
			String qry = "select count(keyword_id) from org_sms_keywords where datediff(date(now()),date(valid_upto)) <= 7  order by org_id";
			
			
			return jdbcTemplate.queryForInt(qry);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception",e);
			return 0;
		}
	}
	public List<OrgSMSkeywords> getExpKeyword(int startIndex, int size) {
	
		List<OrgSMSkeywords> retList = null;
		try {
			String qry = "SELECT distinct k.*,u.emailId,org.to_email_id, u.client_time_zone,u.first_name, u.last_name FROM "
					+ "org_sms_keywords k , users u," +
					" user_organization org WHERE org.user_org_id= k.org_id AND k.user_id = u.user_id  AND " +
					"org.user_org_id = u.user_organization AND org.to_email_id IS NOT NULL AND DATEDIFF(date(k.valid_upto),DATE(now())) <= 7  "
					+ "AND k.status!='"+OCConstants.SMS_KEYWORD_EXPIRED +"' order by u.user_id";
			
			/*String qry = "SELECT k.*,org.to_email_id FROM org_sms_keywords k , " +
					"user_organization org WHERE k.org_id=org.user_org_id AND org.to_email_id" +
					" IS NOT NULL AND DATEDIFF(now(),k.valid_upto) <= 5  order by k.org_id";
			*/
			/*retList = jdbcTemplate.query(qry+" LIMIT "+startIndex+", "+size, new RowMapper() {
			public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
			OrgSMSkeywords keyword = new OrgSMSkeywords();
    		
			keyword.setKeywordId(rs.getLong("keyword_id"));
			keyword.setOrgId(rs.getLong("org_id"));
			
        	 Calendar cal = null;
        	 
        	if(rs.getDate("valid_upto") != null) {
            	cal = Calendar.getInstance();
            	cal.setTime(rs.getTimestamp("valid_upto"));
            	keyword.setValidUpto(cal);
            } else {
            	keyword.setValidUpto(null);
            }
        	 keyword.setKeyword(rs.getString("keyword"));
        	 keyword.setShortCode(rs.getString("short_code"));
        	 
        	 if(rs.getDate("created_date") != null) {
             	cal = Calendar.getInstance();
             	cal.setTime(rs.getTimestamp("created_date"));
             	keyword.setCreatedDate(cal);
             } else {
             	keyword.setCreatedDate(null);
             }
        	 
        	 if(rs.getDate("start_from") != null) {
              	cal = Calendar.getInstance();
              	cal.setTime(rs.getTimestamp("start_from"));
              	keyword.setStartFrom(cal);
              } else {
              	keyword.setStartFrom(null);
              }
        	 
        	 keyword.setStatus(rs.getString("status"));
        	 keyword.setAutoResponse(rs.getString("auto_response"));
        	 
        	 Users user = new Users();
        	 user.setUserId(rs.getLong("user_id"));
        	 user.setFirstName(rs.getString("first_name"));
        	 user.setLastName(rs.getString("last_name"));
        	 user.setEmailId(rs.getString("emailId"));
        	 keyword.setUser(user);
        	 
        	 keyword.setSenderId(rs.getString("sender_id"));
        	 
        	 keyword.setToEmailId(rs.getString("to_email_id"));
        	 keyword.setClientTimeZone(rs.getString("client_time_zone"));
        	 return keyword;
        }
		});
			
	} catch (Exception e) {
		logger.error("Exception ::::" ,e);
	}		
			
	if(retList == null || retList.size() <= 0) {
				
		return null;
	}else{
		return retList;
	}
		
		
	}
	
	public List<String> getToMailIds() {
		
		String qry = "select to_email_id from email_queue where status In "
				+ "('Sent','Active') AND type = 'Alert' AND DATEDIFF(now(),sent_date) < 1";
		
		List<String> subjects = jdbcTemplate.queryForList(qry, String.class);
		
		return subjects;
		
	}*/
	
	public int keywordStatusUpdate(String status) {
		
		String subQry = ( (status.equalsIgnoreCase(Constants.KEYWORD_STATUS_EXPIRED) ? 
				(" DATEDIFF(date(valid_upto), date(now()) ) < 0 AND status!='"+Constants.KEYWORD_STATUS_EXPIRED +"'") 
				:(status.equalsIgnoreCase(Constants.KEYWORD_STATUS_ACTIVE) ? 
						" DATE(NOW()) = DATE(start_from) AND status='"+Constants.KEYWORD_STATUS_PENDING+"'":"")));  
		
		
		String qry = "Update org_sms_keywords set status = '"+status+"' "+ (!subQry.isEmpty() ? (" WHERE "+subQry) : "");
		int affectedKeywords = jdbcTemplate.update(qry);
		return affectedKeywords;
	}

	
}
