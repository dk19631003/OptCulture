package org.mq.marketer.campaign.dao;

import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.NotificationCampaignReport;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class NotificationCampaignReportDao extends AbstractSpringDao {

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
 
	private NotificationCampaignReportDao() {}
 	
	private JdbcTemplate jdbcTemplate;
	
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public NotificationCampaignReport find(Long id) {
        return (NotificationCampaignReport) super.find(NotificationCampaignReport.class, id);
    }

    @SuppressWarnings("unchecked")
	public List<NotificationCampaignReport> findAll() {
        return super.findAll(NotificationCampaignReport.class);
    }
    
    public List<NotificationCampaignReport> getAllReports(String userId,String fromDateStr,String toDateStr, String sourceType, int startIndex, int count,String orderby_colName,String desc_Asc) throws Exception{
 		
 		return getNotificationCampaignReports(null, userId, fromDateStr, toDateStr, false,sourceType, startIndex, count,orderby_colName,desc_Asc);
    }

    public NotificationCampaignReport findById(Long notificationCampaignReportId) {
 	   
	   try {
		   String hql = "FROM NotificationCampaignReport WHERE notificationCrId = "+notificationCampaignReportId.longValue();
		   
		   List<NotificationCampaignReport> retList = executeQuery(hql);
		   
		   if(retList != null && retList.size() > 0) {
			   
			   return retList.get(0);
		   }
		   else return null;
		} catch (Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
		   
		   
		   
   }
    
    public List<NotificationCampaignReport> getNotificationCampaignReports(String notificationCampaignName, 
    		String userId, String fromDateStr, String toDateStr, boolean isLike, String sourceType,  int startIndex, int count,String orderby_colName,String desc_Asc) throws Exception {
    	
    	String checkCon = "";
    	if(sourceType!=null && !sourceType.equalsIgnoreCase("--All--")){
    		checkCon +=" sourceType like '"+sourceType+"%' AND ";
    	}
    	if(notificationCampaignName != null) {
    		checkCon += " notificationCampaignName " + (isLike? ("like '%"+ notificationCampaignName +"%' ") : "='" + notificationCampaignName + "'") + " AND ";
    	}
    	
 		if(userId != null) {
 			checkCon += " userId IN(" + userId + ") AND";
 		} 
 		
 		if(fromDateStr != null && toDateStr != null) {
 			checkCon += " sentDate between '" + fromDateStr + "' AND '" + toDateStr + "' AND ";
 		}
    	
 		String queryStr = " FROM NotificationCampaignReport WHERE " + checkCon + " status IN ('" + Constants.CAMP_STATUS_SENT + "') ORDER BY  "+orderby_colName+" "+desc_Asc;
 		logger.debug("Query : " + queryStr);
    	
 		return executeQuery(queryStr, startIndex, count);
    } //getCampaignReports
    


    public List<NotificationCampaignReport> getReportsByCampaignName(String notificationCampaignName, 
    		String userId, String fromDateStr, String toDateStr, boolean isLike, String sourceType,  int startIndex, int count,String orderby_colName,String desc_Asc) throws Exception {
    	return getNotificationCampaignReports(notificationCampaignName, userId, fromDateStr, toDateStr, isLike, sourceType, startIndex, count,orderby_colName,desc_Asc);
    }

    public List<String> getCampaignList(Long userId){
    	String query="select distinct(notificationCampaignName) from NotificationCampaignReport" +
						" where userId =" + userId + " and  status='" + Constants.CAMP_STATUS_SENT + "' ORDER BY  sentDate DESC ";
    	logger.info("query is----->"+query);
    	return executeQuery(query);
    }
    
    public long getReportCountByCampaign(String notificationCampaignName,Long userId){
    	 
    	return ((Long)executeQuery("select count(*) from NotificationCampaignReport" +
    			" where notificationCampaignName ='" + notificationCampaignName + "' and userId= " + userId).get(0)).longValue();
    }

    public List<NotificationCampaignReport> getReportByNotificationCampaignName(String notificationCampaignName,String userId,  int startIndex, int count,String orderby_colName,String desc_Asc) throws Exception {
    	
    	return getNotificationCampaignReports(notificationCampaignName, userId, null, null, false,null, startIndex, count,orderby_colName,desc_Asc);
    }
    
 public List<NotificationCampaignReport> getReportsByNotificationCampaignName(String notificationCampaignName,Long userId )  {
    	
	 try {
			List<NotificationCampaignReport> repList = null;
			
			String qry  = " FROM NotificationCampaignReport  WHERE userId="+userId+" AND notificationCampaignName ='" + notificationCampaignName + "'" +
							" AND status ='" + Constants.CAMP_STATUS_SENT + "' AND  sourceType = '" + Constants.SOURCE_NOTIFICATION_CAMPAIGN + "' ORDER BY  sentDate DESC";
			
			repList = executeQuery(qry);
			if(repList != null && repList.size() > 0) return repList;
			
			else return null;
			
			
			}catch (Exception e) {
				logger.error("Exception ::" , e);
				return null;
			}
    }
    
    public List<String> getNotificationCampaignList(String userId, String sourceType){
    	String query = "";
    	if(sourceType.equalsIgnoreCase("--All--")){
    		query = "select distinct (notificationCampaignName) from NotificationCampaignReport" +" where userId IN(" +
    					userId + ") and  status='" + Constants.CAMP_STATUS_SENT + "' ORDER BY  sentDate DESC ";
    	}
    	else {
    		query = "select distinct (notificationCampaignName) from NotificationCampaignReport" +
			" where userId IN(" + userId + ") and  sourceType like '"+sourceType+"%' "+"and  status='" + Constants.CAMP_STATUS_SENT + "' ORDER BY  sentDate DESC";
    	}
    	return  executeQuery(query);
    }
    
    
    public List<NotificationCampaignReport> findAllAsAdmin(String fromDateStr, String toDateStr, String sourceType, int startIndex, int count,String userId,String orderby_colName,String desc_Asc) {
    	
    	return findAllReportsAsAdmin(fromDateStr, toDateStr, sourceType, startIndex, count, userId,orderby_colName,desc_Asc);
    	
    	
    	
    }
    
    
    public List<NotificationCampaignReport> findAllReportsAsAdmin(String fromDateStr, String toDateStr, String sourceType, int startIndex, int count,String userId,String orderby_colName,String desc_Asc) {
    	
    	String checkOn = "";
    	String checkUsrId = "";
    	if(sourceType != null && !sourceType.contains("--All--")) {
    		
  		   	checkOn += " AND sourceType like '%"+sourceType+"%'";
  	   	}
  	   	if(userId != null) {
  	   		checkUsrId += " AND userId IN (" +userId + ") ";
  	   	}
  	   	String qry = "FROM NotificationCampaignReport WHERE sentDate between '" + fromDateStr + "' AND '" + toDateStr + "'"+ checkOn + checkUsrId +
  	   			" AND  status IN('" + Constants.CAMP_STATUS_SENT + "') ORDER BY  "+orderby_colName+" "+desc_Asc;
  	   	return executeQuery(qry, startIndex, count);
    }
    
    
    
    public int getReportCount(String userId, String campaignName, boolean isLike, String sourceType, String fromDateStr, String toDateStr) {
 	   
 	   
 	   String checkCon = "";
    	
    	if(campaignName != null) {
    		checkCon += " notificationCampaignName " + (isLike? ("like '%"+ campaignName +"%' ") : "='" + campaignName + "'") + " AND ";
    	}
    	
 		if(userId != null) {
 			checkCon += " userId IN(" + userId + ") AND";
 		} 
 		
 		if(fromDateStr != null && toDateStr != null) {
 			checkCon += " sentDate between '" + fromDateStr + "' AND '" + toDateStr + "' AND ";
 		}
 		if(sourceType != null && !sourceType.equalsIgnoreCase("--All--")) {
 			checkCon += " sourceType like '"+sourceType+"%'  AND ";
 		}
    	
 		String queryStr = " select COUNT(notificationCrId) FROM NotificationCampaignReport WHERE " + checkCon + " status IN ('" + Constants.CAMP_STATUS_SENT + "') ORDER BY  sentDate DESC";
 		logger.debug("Query  : " + queryStr);
 	   
 	   
 	   
 	   return ((Long)executeQuery(queryStr).get(0)).intValue();
 	   
 	   
    }
    
    
    @SuppressWarnings("unchecked")
	public List<NotificationCampaignReport> getRecentNotificationCampignReportList(Long userId){
    	List<NotificationCampaignReport> notificationCampaignReportList = null;
    	try{
    		String qry = "from NotificationCampaignReport where userId =" + userId +
    				" and status in ('"+ Constants.SOCIAL_CAMP_STATUS_RUNNING +"','" + Constants.CAMP_STATUS_SENT + "') AND sourceType Like '" + Constants.SOURCE_NOTIFICATION_CAMPAIGN + "%' order by sentDate desc";
    		
    		notificationCampaignReportList = executeQuery(qry, 0, 7);
    		
    	}catch (Exception e){
			logger.error(" ** Exception : Error while getting the recent campaigns report"+ e +" **");
		}
    	return notificationCampaignReportList;
    }
    
    
    public List<Object[]> findDeliveredCount(String notificationCrids) {
    	
    	try {
			String queryStr = " SELECT DISTINCT(sr), COUNT(scs.sentId) AS delivered FROM NotificationCampaignReport sr, NotificationCampaignSent scs "
					+ "WHERE sr.notificationCrId=scs.notificationCampaignReport.notificationCrId AND sr.notificationCrId IN("+notificationCrids+") AND scs.status "
							+ "IN('Sent') AND sr.status IN ('Sent') GROUP BY scs.notificationCampaignReport.notificationCrId"
							;
			logger.debug("Query : " + queryStr);
			
			return executeQuery(queryStr);
		} catch (Exception e) {
			logger.error("Exception ", e);
			return null;
		}
    	
    	
    }
    
    public long getTodayMessages(Long userId) {
		try {
			
			String qry="SELECT SUM(sent) FROM NotificationCampaignReport WHERE userId ="+ userId.longValue() + 
					" AND DATEDIFF(now(), sentDate)=0 ";

			List list = executeQuery(qry);
		
			if(list.size() > 0 && list.get(0) != null) {
				Object obj = list.get(0);
				logger.debug("obj "+obj);
				if(obj instanceof Long){
					
					return ((Long)obj).longValue();
				}
			} // if
			
		} catch (Exception e) {
			logger.error("Exception ::" , e);
		}
		return 0;
	} // getTodayMessages

	public List<Object[]> findFaliedCount(String notificationCrids) {
		try {
			String queryStr = " SELECT DISTINCT(sr), COUNT(scs.sentId) AS failed FROM NotificationCampaignReport sr, NotificationCampaignSent scs "
					+ "WHERE sr.notificationCrId=scs.notificationCampaignReport.notificationCrId AND sr.notificationCrId IN("+notificationCrids+") AND scs.status "
							+ "IN('Failure') AND sr.status IN ('Sent') GROUP BY scs.notificationCampaignReport.notificationCrId"
							;
			logger.debug("Query : " + queryStr);
			
			return executeQuery(queryStr);
		} catch (Exception e) {
			logger.error("Exception ", e);
			return null;
		}
	}
	
	public List<NotificationCampaignReport> getNotificationSentByUserId(Long userId, String notificationCampName, String bannerImageName) {
		List<NotificationCampaignReport> notificationCampReportList = null;
		String query = "FROM NotificationCampaignReport ncr where ncr.userId = '"+userId+"' "
						+ " AND  ncr.notificationCampaignName='"+notificationCampName+"'"
						+ " AND ncr.notificationLogoImage='"+bannerImageName.trim()+"' "
						+ " AND status='Sent'";
		notificationCampReportList = executeQuery(query);
		return notificationCampReportList;		
	}

}
