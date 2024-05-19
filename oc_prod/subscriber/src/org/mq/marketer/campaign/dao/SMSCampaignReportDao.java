package org.mq.marketer.campaign.dao;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.beans.SMSCampaignReport;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.SMSStatusCodes;

public class SMSCampaignReportDao extends AbstractSpringDao {

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
 
	public SMSCampaignReportDao() {}
 	
	private SMSCampaignSentDao smsCampaignSentDao = null;
	
	public SMSCampaignSentDao getSmsCampaignSentDao() {
		return smsCampaignSentDao;
	}
	
	public void setSmsCampaignSentDao(SMSCampaignSentDao smsCampaignSentDao) {
		this.smsCampaignSentDao = smsCampaignSentDao;
	}

	public SMSCampaignReport find(Long id) {
        return (SMSCampaignReport) super.find(SMSCampaignReport.class, id);
    }

   /* public void saveOrUpdate(SMSCampaignReport smsCampaignReport) {
        super.saveOrUpdate(smsCampaignReport);
    }

    public void delete(SMSCampaignReport smsCampaignReport) {
        super.delete(smsCampaignReport);
    }*/

    public List findAll() {
        return super.findAll(SMSCampaignReport.class);
    }
    
    /*public void deleteByCollection(Collection smsCampaignReportCollection){
    	getHibernateTemplate().deleteAll(smsCampaignReportCollection);
    }*/
    
    public List<SMSCampaignReport> getAllReports(String userId,String fromDateStr,String toDateStr, String sourceType, int startIndex, int count,String orderby_colName,String desc_Asc) throws Exception{
 		
 		return getSmsCampaignReports(null, userId, fromDateStr, toDateStr, false,sourceType, startIndex, count,orderby_colName,desc_Asc);
    }

    public SMSCampaignReport findById(Long smsCampaignReportId) {
 	   
	   try {
		   String hql = "FROM SMSCampaignReport WHERE smsCrId = "+smsCampaignReportId.longValue();
		   
		   List<SMSCampaignReport> retList = executeQuery(hql);
		   
		   if(retList != null && retList.size() > 0) {
			   
			   return retList.get(0);
		   }
		   else return null;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::" , e);
			return null;
		}
		   
		   
		   
   }
    
    public List<SMSCampaignReport> getSmsCampaignReports(String smsCampaignName, 
    		String userId, String fromDateStr, String toDateStr, boolean isLike, String sourceType,  int startIndex, int count,String orderby_colName,String desc_Asc) throws Exception {
    	
    	String checkCon = "";
    	if(sourceType!=null && !sourceType.equalsIgnoreCase("--All--")){
    		checkCon +=" sourceType like '"+sourceType+"%' AND ";
    	}
    	if(smsCampaignName != null) {
    		checkCon += " smsCampaignName " + (isLike? ("like '%"+ smsCampaignName +"%' ") : "='" + smsCampaignName + "'") + " AND ";
    	}
    	
 		if(userId != null) {
 			checkCon += " user IN(" + userId + ") AND";
 		} 
 		
 		if(fromDateStr != null && toDateStr != null) {
 			checkCon += " sentDate between '" + fromDateStr + "' AND '" + toDateStr + "' AND ";
 		}
    	
 		String queryStr = " FROM SMSCampaignReport WHERE " + checkCon + " status IN ('" + Constants.CAMP_STATUS_SENT + "') ORDER BY  "+orderby_colName+" "+desc_Asc;
 		logger.debug("Query : " + queryStr);
    	
 		return executeQuery(queryStr, startIndex, count);
    } //getCampaignReports
    


    public List<SMSCampaignReport> getReportsByCampaignName(String smsCampaignName, 
    		String userId, String fromDateStr, String toDateStr, boolean isLike, String sourceType,  int startIndex, int count,String orderby_colName,String desc_Asc) throws Exception {
    	return getSmsCampaignReports(smsCampaignName, userId, fromDateStr, toDateStr, isLike, sourceType, startIndex, count,orderby_colName,desc_Asc);
    }

    public List<String> getCampaignList(Long userId){
    	String query="select distinct(smsCampaignName) from SMSCampaignReport" +
						" where user =" + userId + " and  status='" + Constants.CAMP_STATUS_SENT + "' ORDER BY  sentDate DESC ";
    	logger.info("query is----->"+query);
    	return getHibernateTemplate().find(query);
    }
    
    public long getReportCountByCampaign(String smsCampaignName,Long userId){
    	 
    	return ((Long)getHibernateTemplate().find("select count(*) from SMSCampaignReport" +
    			" where smsCampaignName ='" + smsCampaignName + "' and user= " + userId).get(0)).longValue();
    }

    public List<SMSCampaignReport> getReportBySmsCampaignName(String smsCampaignName,String userId,  int startIndex, int count,String orderby_colName,String desc_Asc) throws Exception {
    	
    	return getSmsCampaignReports(smsCampaignName, userId, null, null, false,null, startIndex, count,orderby_colName,desc_Asc);
    }
    
 public List<SMSCampaignReport> getReportsBySmsCampaignName(String smsCampaignName,Long userId )  {
    	
	 try {
			List<SMSCampaignReport> repList = null;
			
			String qry  = " FROM SMSCampaignReport  WHERE user="+userId+" AND smsCampaignName ='" + smsCampaignName + "'" +
							" AND status ='" + Constants.CAMP_STATUS_SENT + "' AND  sourceType = '" + Constants.SOURCE_SMS_CAMPAIGN + "' ORDER BY  sentDate DESC";
			
			repList = executeQuery(qry);
			if(repList != null && repList.size() > 0) return repList;
			
			else return null;
			
			
			}catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return null;
			}
    }
    
    public List<String> getSMSCampaignList(String userId, String sourceType){
    	String query = "";
    	if(sourceType.equalsIgnoreCase("--All--")){
    		query = "select distinct smsCampaignName from SMSCampaignReport" +" where user IN(" +
    					userId + ") and  status='" + Constants.CAMP_STATUS_SENT + "' ORDER BY  sentDate DESC ";
    	}
    	else {
    		query = "select distinct smsCampaignName from SMSCampaignReport" +
			" where user IN(" + userId + ") and  sourceType like '"+sourceType+"%' "+"and  status='" + Constants.CAMP_STATUS_SENT + "' ORDER BY  sentDate DESC";
    	}
    	logger.info("getSMSCampaignListQuery is"+query);
    	return getHibernateTemplate().find(query);
    }
    
    
    public List<SMSCampaignReport> findAllAsAdmin(String fromDateStr, String toDateStr, String sourceType, int startIndex, int count,String userId,String orderby_colName,String desc_Asc) {
    	
    	return findAllReportsAsAdmin(fromDateStr, toDateStr, sourceType, startIndex, count, userId,orderby_colName,desc_Asc);
    	
    	
    	
    }
    
    
    public List<SMSCampaignReport> findAllReportsAsAdmin(String fromDateStr, String toDateStr, String sourceType, int startIndex, int count,String userId,String orderby_colName,String desc_Asc) {
    	
    	List<SMSCampaignReport> smsCampRepList = null;
    	String checkOn = "";
    	String checkUsrId = "";
    	if(sourceType != null && !sourceType.contains("--All--")) {
    		
  		   	checkOn += " AND sourceType like '%"+sourceType+"%'";
  	   	}
  	   	if(userId != null) {
  	   		checkUsrId += " AND user IN (" +userId + ") ";
  	   	}
  	   	String qry = "FROM SMSCampaignReport WHERE sentDate between '" + fromDateStr + "' AND '" + toDateStr + "'"+ checkOn + checkUsrId +
  	   			" AND  status IN('" + Constants.CAMP_STATUS_SENT + "') ORDER BY  "+orderby_colName+" "+desc_Asc;
  	   	return executeQuery(qry, startIndex, count);
    }
    
    
    
    public int getReportCount(String userId, String campaignName, boolean isLike, String sourceType, String fromDateStr, String toDateStr) {
 	   
 	   
 	   String checkCon = "";
    	
    	if(campaignName != null) {
    		checkCon += " smsCampaignName " + (isLike? ("like '%"+ campaignName +"%' ") : "='" + campaignName + "'") + " AND ";
    	}
    	
 		if(userId != null) {
 			checkCon += " user IN(" + userId + ") AND";
 		} 
 		
 		if(fromDateStr != null && toDateStr != null) {
 			checkCon += " sentDate between '" + fromDateStr + "' AND '" + toDateStr + "' AND ";
 		}
 		if(sourceType != null && !sourceType.equalsIgnoreCase("--All--")) {
 			checkCon += " sourceType like '"+sourceType+"%'  AND ";
 		}
    	
 		String queryStr = " select COUNT(smsCrId) FROM SMSCampaignReport WHERE " + checkCon + " status IN ('" + Constants.CAMP_STATUS_SENT + "') ORDER BY  sentDate DESC";
 		logger.debug("Query  : " + queryStr);
 	   
 	   
 	   
 	   return ((Long)getHibernateTemplate().find(queryStr).get(0)).intValue();
 	   
 	   
    }
    
    
    public List<SMSCampaignReport> getRecentSmsCampignReportList(Long userId){
    	List smsCampaignReportList = null;
    	try{
    		String qry = "from SMSCampaignReport where user =" + userId +
    				" and status in ('"+ Constants.SOCIAL_CAMP_STATUS_RUNNING +"','" + Constants.CAMP_STATUS_SENT + "') AND sourceType Like '" + Constants.SOURCE_SMS_CAMPAIGN + "%' order by sentDate desc";
    		
    		smsCampaignReportList = executeQuery(qry, 0, 7);
    		
    	}catch (Exception e){
			logger.error(" ** Exception : Error while getting the recent campaigns report"+ e +" **");
		}
    	return smsCampaignReportList;
    }
    
    
    public List<Object[]> findDeliveredCount(String smsCrids) {
    	
    	try {
			String queryStr = " SELECT DISTINCT(sr), COUNT(scs.sentId) AS delivered FROM SMSCampaignReport sr, SMSCampaignSent scs "
					+ "WHERE sr.smsCrId=scs.smsCampaignReport.smsCrId AND sr.smsCrId IN("+smsCrids+") AND scs.status "
							+ "IN('"+SMSStatusCodes.CLICKATELL_STATUS_RECEIVED+"') AND sr.status IN ('" + Constants.CAMP_STATUS_SENT + "') GROUP BY scs.smsCampaignReport.smsCrId"
							;
			logger.debug("Query : " + queryStr);
			
			return executeQuery(queryStr);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ", e);
			return null;
		}
    	
    	
    }
    
    public long getTodayMessages(Long userId) {
		try {
			
			String qry="SELECT SUM(sent) FROM SMSCampaignReport WHERE user ="+ userId.longValue() + 
					" AND DATEDIFF(now(), sentDate)=0 ";

			List list = getHibernateTemplate().find(qry);
		
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
    
	public List<SMSCampaignReport> getReportsBySmsCampaignNameWithInaRange(String smsCampaignName, Long userId,
			String fromDate, String todate) {

		try {
			List<SMSCampaignReport> repList = null;

			String qry = " FROM SMSCampaignReport  WHERE user=" + userId + " AND smsCampaignName ='" + smsCampaignName
					+ "'" + " AND status ='" + Constants.CAMP_STATUS_SENT + "' AND  sourceType = '"
					+ Constants.SOURCE_SMS_CAMPAIGN + "'" + " AND sentDate between '" + fromDate + "' AND '" + todate
					+ "'" + " ORDER BY  sentDate DESC";

			repList = executeQuery(qry);
			if (repList != null && repList.size() > 0)
				return repList;

			else
				return null;

		} catch (Exception e) {
			// TODO Auto-generated catch block
			logger.error("Exception ::", e);
			return null;
		}
	}
    
}
