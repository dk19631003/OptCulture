package org.mq.marketer.campaign.dao;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.marketer.campaign.beans.CampaignReport;
import org.mq.marketer.campaign.beans.WACampaignReport;
import org.mq.marketer.campaign.general.Constants;
import org.mq.marketer.campaign.general.WAStatusCodes;

public class WACampaignReportDao extends AbstractSpringDao {

	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
 
	public WACampaignReportDao() {}
 	
	private WACampaignSentDao waCampaignSentDao = null;
	
	public WACampaignSentDao getWaCampaignSentDao() {
		return waCampaignSentDao;
	}
	
	public void setWaCampaignSentDao(WACampaignSentDao waCampaignSentDao) {
		this.waCampaignSentDao = waCampaignSentDao;
	}

	public WACampaignReportDao find(Long id) {
        return (WACampaignReportDao) super.find(WACampaignReport.class, id);
    }

    public List findAll() {
        return super.findAll(WACampaignReport.class);
    }
    
    public List<WACampaignReport> getAllReports(String userId,String fromDateStr,String toDateStr, String sourceType, int startIndex, int count,String orderby_colName,String desc_Asc) throws Exception{
 		
 		return getWaCampaignReports(null, userId, fromDateStr, toDateStr, false,sourceType, startIndex, count,orderby_colName,desc_Asc);
    }

    public WACampaignReport findById(Long waCampaignReportId) {
 	   
	   try {
		   String hql = "FROM WACampaignReport WHERE waCrId = "+waCampaignReportId.longValue();
		   
		   List<WACampaignReport> retList = executeQuery(hql);
		   
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
    
    public List<WACampaignReport> getWaCampaignReports(String waCampaignName, 
    		String userId, String fromDateStr, String toDateStr, boolean isLike, String sourceType,  int startIndex, int count,String orderby_colName,String desc_Asc) throws Exception {
    	
    	String checkCon = "";
    	if(sourceType!=null && !sourceType.equalsIgnoreCase("--All--")){
    		checkCon +=" sourceType like '"+sourceType+"%' AND ";
    	}
    	if(waCampaignName != null) {
    		checkCon += " waCampaignName " + (isLike? ("like '%"+ waCampaignName +"%' ") : "='" + waCampaignName + "'") + " AND ";
    	}
    	
 		if(userId != null) {
 			checkCon += " user IN(" + userId + ") AND";
 		} 
 		
 		if(fromDateStr != null && toDateStr != null) {
 			checkCon += " sentDate between '" + fromDateStr + "' AND '" + toDateStr + "' AND ";
 		}
    	
 		String queryStr = " FROM WACampaignReport WHERE " + checkCon + " status IN ('" + Constants.CAMP_STATUS_SENT + "') ORDER BY  "+orderby_colName+" "+desc_Asc;
 		logger.debug("Query : " + queryStr);
    	
 		return executeQuery(queryStr, startIndex, count);
    } //getCampaignReports
    


    public List<WACampaignReport> getReportsByCampaignName(String waCampaignName, 
    		String userId, String fromDateStr, String toDateStr, boolean isLike, String sourceType,  int startIndex, int count,String orderby_colName,String desc_Asc) throws Exception {
    	return getWaCampaignReports(waCampaignName, userId, fromDateStr, toDateStr, isLike, sourceType, startIndex, count,orderby_colName,desc_Asc);
    }

    public List<String> getCampaignList(Long userId){
    	String query="select distinct(waCampaignName) from WACampaignReport" +
						" where user =" + userId + " and  status='" + Constants.CAMP_STATUS_SENT + "' ORDER BY  sentDate DESC ";
    	logger.info("query is----->"+query);
    	return getHibernateTemplate().find(query);
    }
    
    public long getReportCountByCampaign(String waCampaignName,Long userId){
    	 
    	return ((Long)getHibernateTemplate().find("select count(*) from WACampaignReport" +
    			" where waCampaignName ='" + waCampaignName + "' and user= " + userId).get(0)).longValue();
    }

    public List<WACampaignReport> getReportByWaCampaignName(String waCampaignName,String userId,  int startIndex, int count,String orderby_colName,String desc_Asc) throws Exception {
    	
    	return getWaCampaignReports(waCampaignName, userId, null, null, false,null, startIndex, count,orderby_colName,desc_Asc);
    }
    
 public List<WACampaignReport> getReportsByWaCampaignName(String waCampaignName,Long userId )  {
    	
	 try {
			List<WACampaignReport> repList = null;
			
			String qry  = " FROM WACampaignReport  WHERE user="+userId+" AND waCampaignName ='" + waCampaignName + "'" +
							" AND status ='" + Constants.CAMP_STATUS_SENT + "' AND  sourceType = '" + Constants.SOURCE_WA_CAMPAIGN + "' ORDER BY  sentDate DESC";
			
			repList = executeQuery(qry);
			if(repList != null && repList.size() > 0) return repList;
			
			else return null;
			
			
			}catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception ::" , e);
				return null;
			}
    }
    
    public List<String> getWACampaignList(String userId, String sourceType){
    	String query = "";
    	if(sourceType.equalsIgnoreCase("--All--")){
    		query = "select distinct waCampaignName from WACampaignReport" +" where user IN(" +
    					userId + ") and  status='" + Constants.CAMP_STATUS_SENT + "' ORDER BY  sentDate DESC ";
    	}
    	else {
    		query = "select distinct waCampaignName from WACampaignReport" +
			" where user IN(" + userId + ") and  sourceType like '"+sourceType+"%' "+"and  status='" + Constants.CAMP_STATUS_SENT + "' ORDER BY  sentDate DESC";
    	}
    	logger.info("getWACampaignListQuery is"+query);
    	return getHibernateTemplate().find(query);
    }
    
    
    public List<WACampaignReport> findAllAsAdmin(String fromDateStr, String toDateStr, String sourceType, int startIndex, int count,String userId,String orderby_colName,String desc_Asc) {
    	
    	return findAllReportsAsAdmin(fromDateStr, toDateStr, sourceType, startIndex, count, userId,orderby_colName,desc_Asc);
    	
    	
    	
    }
    
    
    public List<WACampaignReport> findAllReportsAsAdmin(String fromDateStr, String toDateStr, String sourceType, int startIndex, int count,String userId,String orderby_colName,String desc_Asc) {
    	
    	List<WACampaignReport> waCampRepList = null;
    	String checkOn = "";
    	String checkUsrId = "";
    	if(sourceType != null && !sourceType.contains("--All--")) {
    		
  		   	checkOn += " AND sourceType like '%"+sourceType+"%'";
  	   	}
  	   	if(userId != null) {
  	   		checkUsrId += " AND user IN (" +userId + ") ";
  	   	}
  	   	String qry = "FROM WACampaignReport WHERE sentDate between '" + fromDateStr + "' AND '" + toDateStr + "'"+ checkOn + checkUsrId +
  	   			" AND  status IN('" + Constants.CAMP_STATUS_SENT + "') ORDER BY  "+orderby_colName+" "+desc_Asc;
  	   	return executeQuery(qry, startIndex, count);
    }
    
    
    
    public int getReportCount(String userId, String campaignName, boolean isLike, String sourceType, String fromDateStr, String toDateStr) {
 	   
 	   
 	   String checkCon = "";
    	
    	if(campaignName != null) {
    		checkCon += " waCampaignName " + (isLike? ("like '%"+ campaignName +"%' ") : "='" + campaignName + "'") + " AND ";
    	}
    	
 		if(userId != null) {
 			checkCon += " user IN(" + userId + ") AND";
 		} 
 		
 		if(fromDateStr != null && toDateStr != null) {
 			checkCon += " sentDate between '" + fromDateStr + "' AND '" + toDateStr + "' AND ";
 		}
 		if(sourceType != null && !sourceType.equalsIgnoreCase("--All--")) {
 			if(sourceType.equalsIgnoreCase("WACampaignSchedule")) sourceType="WACampaignsSchedule";
 			checkCon += " sourceType like '"+sourceType+"%'  AND ";
 		}
    	
 		String queryStr = " select COUNT(waCrId) FROM WACampaignReport WHERE " + checkCon + " status IN ('" + Constants.CAMP_STATUS_SENT + "') ORDER BY  sentDate DESC";
 		logger.debug("Query  : " + queryStr);
 	   
 	   
 	   
 	   return ((Long)getHibernateTemplate().find(queryStr).get(0)).intValue();
 	   
 	   
    }
    
    
    public List<WACampaignReport> getRecentWaCampignReportList(Long userId){
    	List waCampaignReportList = null;
    	try{
    		String qry = "from WACampaignReport where user =" + userId +
    				" and status in ('"+ Constants.SOCIAL_CAMP_STATUS_RUNNING +"','" + Constants.CAMP_STATUS_SENT + "') AND sourceType Like '" + Constants.SOURCE_WA_CAMPAIGN + "%' order by sentDate desc";
    		
    		waCampaignReportList = executeQuery(qry, 0, 7);
    		
    	}catch (Exception e){
			logger.error(" ** Exception : Error while getting the recent campaigns report"+ e +" **");
		}
    	return waCampaignReportList;
    }
    
    
    public List<Object[]> findDeliveredCount(String waCrids) {
    	
    	try {
			String queryStr = " SELECT DISTINCT(sr), COUNT(scs.sentId) AS delivered FROM WACampaignReport sr, WACampaignSent scs "
					+ "WHERE sr.waCrId=scs.waCampaignReport.waCrId AND sr.waCrId IN("+waCrids+") AND scs.status "
							+ "IN('"+WAStatusCodes.WA_STATUS_RECEIVED+"') AND sr.status IN ('" + Constants.CAMP_STATUS_SENT + "') GROUP BY scs.waCampaignReport.waCrId"
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
			
			String qry="SELECT SUM(sent) FROM WACampaignReport WHERE user ="+ userId.longValue() + 
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
    
	public List<WACampaignReport> getReportsByWaCampaignNameWithInaRange(String waCampaignName, Long userId,
			String fromDate, String todate) {

		try {
			List<WACampaignReport> repList = null;

			String qry = " FROM WACampaignReport  WHERE user=" + userId + " AND waCampaignName ='" + waCampaignName
					+ "'" + " AND status ='" + Constants.CAMP_STATUS_SENT + "' AND  sourceType = '"
					+ Constants.SOURCE_WA_CAMPAIGN + "'" + " AND sentDate between '" + fromDate + "' AND '" + todate
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
