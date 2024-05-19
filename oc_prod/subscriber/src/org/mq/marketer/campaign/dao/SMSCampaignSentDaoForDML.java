package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.SMSCampaignSent;
import org.mq.marketer.campaign.controller.service.ClickaTellApi;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.SMSStatusCodes;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class SMSCampaignSentDaoForDML extends AbstractSpringDaoForDML{
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	private SessionFactory sessionFactory;
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public SMSCampaignSentDaoForDML() {}
  
   /* public SMSCampaignSent find(Long id) {
        return (SMSCampaignSent) super.find(SMSCampaignSent.class, id);
    }*/

    public void saveOrUpdate(SMSCampaignSent smsCampaignSent) {
        super.saveOrUpdate(smsCampaignSent);
    }

    public void delete(SMSCampaignSent smsCampaignSent) {
        super.delete(smsCampaignSent);
    }

    public void updateStatus(String status,String refId,String mobile) {	
		
    	//common method for Equence & Synapse
		//String updateQry = " UPDATE sms_campaign_sent SET status='"+status+"' WHERE sent_id="+sentId+"";
    	/*if(mobile.startsWith("91")&& mobile.length() == 12) {
			
			
			mobile = mobile.substring(2);
		}*/
    	String updateQry = " UPDATE sms_campaign_sent SET status='"+status+"' WHERE api_msg_id='"+refId+"' AND mobile_number = '"+mobile+"'";
		
		logger.debug(updateQry);		
		executeJdbcUpdateQuery(updateQry);		
	
	}
    public void updateBounceStatus(String status,Long sentId) {	
		
		String updateQry = " UPDATE sms_campaign_sent SET status='"+status+"' WHERE sent_id="+sentId+"";
		
		logger.debug(updateQry);		
		executeJdbcUpdateQuery(updateQry);		
	
	}
    
    public void setUniqueMsgId(String msgId,String mrId,String mobile) {	
		
		//String updateQry = " UPDATE sms_campaign_sent SET api_msg_id='"+msgId+"' WHERE sent_id="+sentId+"";
    	String updateQry = " UPDATE sms_campaign_sent SET api_msg_id='"+msgId+"' WHERE api_msg_id='"+mrId+"' AND mobile_number = '"+mobile+"'";
		
		logger.info(updateQry);		
		executeJdbcUpdateQuery(updateQry);		
	
	}
    /*public List findAll() {
        return super.findAll(SMSCampaignSent.class);
    }*/
	
    
    /*public int getCount(Long smsCampRepId, String status) {
    	
    	String qry = "SELECT count(sentId) FROM SMSCampaignSent where smsCampaignReport="+smsCampRepId+" AND status IN("+status+")";
		int count = ((Long)getHibernateTemplate().find(qry).get(0)).intValue();	
		return count;
    	
    	
    	
    	
    }
    
    
    public List<Object[]> getAllMobileNumsByCrId(Long smsCrId,int firstResult,int maxResults) {
		  try {
			  String query = "select sentId,mobileNumber,opens,clicks,status from SMSCampaignSent as cs where  cs.smsCampaignReport =" + smsCrId;

			  List cList = executeQuery(query, firstResult, maxResults);
			  logger.info("list is---->"+cList+" "+cList.size());
			  return cList;
		  } catch (Exception e) {
			  logger.error("** Error : " + e.getMessage() + " **");
			  return null;
		  } 
	  }
	
	public List<String> getUndeliveredResonsByCampReportId(Long smsCampReportId) {
		List<String> reasons = null;
		try {
			
			String query = "SELECT distinct(status) from SMSCampaignSent where smsCampaignReport ="+smsCampReportId+
							" and status not in('" + Constants.SMS_CAMPAIGN_SUCCESS + "')";
			reasons = executeQuery(query);
			reasons.add(0, "All");
				return reasons;
			
		} catch (Exception e) {
			logger.error("Exception :::",e);
			return null;
		}
	}
	
	public List<SMSCampaignSent> getCampSentByUndeliveredCategory(Long smsCampReportId,String status) {
		
		List<SMSCampaignSent> smsCampSentList = null;
		String query ="";
		try {
			if(status.equalsIgnoreCase(Constants.SMS_CAMPAIGN_SENT_STATUS_ALL)){
				query = "from SMSCampaignSent where smsCampaignReport ="+smsCampReportId+
							" and status not in('" + Constants.SMS_CAMPAIGN_SUCCESS + "')";
			}else {
				
				query = " from SMSCampaignSent where smsCampaignReport ="+smsCampReportId+
						" and status = '"+status+"'";
				
			}
			
			smsCampSentList = executeQuery(query);
				return smsCampSentList;
			
		} catch (Exception e) {
			logger.error("Exception :::",e);
			return null;
		}
		
	}
	
	
	 public List getCategoryPercentageByCrId( Long campRepId) {
	    	try {
	    		List<Object[]> catList = new ArrayList<Object[]>();
	    		String queryStr = "SELECT status, COUNT(sent_id)" +
	    				" FROM  sms_campaign_sent  " +
	    				"WHERE sms_cr_id="+campRepId+
	    				"  GROUP BY  status";
	    		logger.info("Query :" + queryStr);
	    		catList = jdbcTemplate.query(queryStr,	new RowMapper(){
	    			Object[] obj;
					@Override
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						obj = new Object[2];
						obj[0] = rs.getString(1);
						obj[1] = rs.getLong(2);
						
						
						return obj;
					}
	    			
	    		});
	    		return catList;
	    		String queryStr = "SELECT b.category, ROUND( (COUNT(b.bounceId) /cr.sent) * 100 , 2)," +
	    		" cr.crId FROM CampaignReport cr , Bounces b " +
	    		"WHERE cr.crId = b.crId " +
	    		"AND cr.user.userId = " + userId + " GROUP BY cr.crId, b.category";
	    		//return executeQuery(queryStr);
	    		
	    	} catch (Exception e) {
	    		logger.error("** Error : " + e.getMessage() + " **");
	    		return null;
			}
	    }
	
	*/
	
	public void updateSmsCampSentStatus(String qryStr) {
		
		getJdbcTemplate().execute(qryStr);
		
		
		
	}
	
	
	
	/*public int getNDNCRejectedCount(Long smsCampRepId) {
		
		String qry = "SELECT count(sentId) FROM SMSCampaignSent where smsCampaignReport="+smsCampRepId+" AND status='" + Constants.NDNC_REJECTED + "'";
		int NDNCCount = ((Long)getHibernateTemplate().find(qry).get(0)).intValue();	
		return NDNCCount;
		
	}
	
	
	public List<String> getNDNCRejected(Long smsCampRepId, int startFrom, int count) {
		String queryStr = "select mobileNumber from SMSCampaignSent where smsCampaignReport="+smsCampRepId+" AND status='" + Constants.NDNC_REJECTED + "'";
		
		List<String> list = (List<String>)executeQuery(queryStr, startFrom, count);
		
		return list;
	}
	
	
	
	public List<String> getRecieved(Long smsCampRepId, int startFrom, int count) {
		
		String queryStr = "select mobileNumber from SMSCampaignSent where " +
				"smsCampaignReport="+smsCampRepId+" AND status='"+SMSStatusCodes.CLICKATELL_STATUS_RECEIVED+"'";
		
		List<String> list = (List<String>)executeQuery(queryStr, startFrom, count);
		
		return list;
	}
	
	
	
	public List<Object[]> getRepByStatus(Long smsCampRepId, String status, int startFrom, int count) {
		
		
		String qry = "SELECT mobileNumber, opens,clicks, status From SMSCampaignSent WHERE smsCampaignReport="+smsCampRepId+
						" AND status IN ("+status+")";
		logger.info("query :" +qry);
		return executeQuery(qry, startFrom, count );
		
	}//getRepByStatus
	
	
	
	public List<String> getDelivered(Long smsCampRepId, int startFrom, int count) {
		String queryStr = "select mobileNumber from SMSCampaignSent where smsCampaignReport="+smsCampRepId+" AND status='"+SMSStatusCodes.CLICKATELL_STATUS_DELIVERED_TO_RECEPIENT+"'";
		
		List<String> list = (List<String>)executeQuery(queryStr, startFrom, count);
		
		return list;
	}
	
	
	public List<String> getOptedOut(Long smsCampRepId, int startFrom, int count) {
		String queryStr = "select mobileNumber from SMSCampaignSent where smsCampaignReport="+smsCampRepId+" AND status='"+SMSStatusCodes.CLICKATELL_STATUS_OPTED_OUT+"'";
		
		List<String> list = (List<String>)executeQuery(queryStr, startFrom, count);
		
		return list;
	}
	
	
	 public List<Object[]> getOpenedMobilesByCrId(Long crId,int firstResult,int maxResults) {
		  try {
			  //String query = "select cs.sentId,cs.emailId,cs.opens,o.openDate from CampaignSent as cs, Opens  o where  cs.campaignReport =" + crId +" and cs.opens>0 and cs.sentId=o.sentId and o.openDate=(SELECT max(op.openDate) FROM Opens op where op.sentId=cs.sentId) order by cs.opens desc";
			  String query = "SELECT o.sentId, (SELECT c.mobileNumber FROM SMSCampaignSent c WHERE c.smsCampaignReport=" + crId +
			  " AND o.sentId=c.sentId), COUNT(o.sentId), MAX(o.openDate) FROM SMSOpens o WHERE o.sentId IN (SELECT cs.sentId FROM SMSCampaignSent cs WHERE cs.smsCampaignReport=" + crId +
			  " AND cs.opens>0) GROUP BY o.sentId  ORDER BY COUNT(o.sentId) DESC";
			  
			  List cList = executeQuery(query, firstResult, maxResults);
			  return cList;
		  } catch (Exception e) {
			  logger.error("** Error : " + e.getMessage() + " **");
			  return null;
		  } 
	  }
	  
	  public List<Object[]> getClickedMobilesByCrId(Long crId,int firstResult,int maxResults) {
		  try {
			  String query = "select cs.sentId,cs.mobileNumber,cs.clicks,c.clickUrl from SMSCampaignSent " +
			  		"as cs, SMSClicks  c where  cs.smsCampaignReport =" + crId +" and cs.clicks>0 and " +
			  				"cs.sentId=c.sentId and c.clickDate=(SELECT max(ck.clickDate) FROM SMSClicks ck " +
			  				"where ck.sentId=cs.sentId) group by cs.sentId order by cs.clicks desc";

			  List cList = executeQuery(query, firstResult, maxResults);
			  return cList;
		  } catch (Exception e) {
			  logger.error("** Error : " + e.getMessage() + " **");
			  return null;
		  } 
	  }
	  
	  public List<Object[]> getAllSentCategories(Long smsCampRepId) {
			 
			 String queryStr = "select  status, count(sent_id) from sms_campaign_sent where status Not " +
			 		"IN('"+ Constants.SMS_CAMPAIGN_STATUS_BOUNCE + "','" + Constants.SMS_CAMPAIGN_STATUS_PENDING + "','" +
			 		SMSStatusCodes.CLICKATELL_STATUS_RECEIVED+ "') And sms_cr_id =" + smsCampRepId + " group by status order by count(sent_id)  desc";
			 
			 List<Object[]> catList = new ArrayList<Object[]>();
			 catList = jdbcTemplate.query(queryStr,	new RowMapper(){
	 			Object[] obj;
					@Override
					public Object mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						obj = new Object[2];
						obj[0] = rs.getString(1);
						obj[1] = rs.getLong(2);
						
						
						return obj;
					}
	 			
	 		});
	 		return catList;
			 
		 }
	
	  public int findTotClicksByCrid(Long crId) throws Exception{
		  
		  
		String  queryString = "SELECT SUM(clicks) FROM SMSCampaignSent WHERE smsCampaignReport="+crId.longValue() ;
		return ((Long) executeQuery(queryString).get(0)).intValue();
		  
	  }*/
}
