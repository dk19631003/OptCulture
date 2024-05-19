


package org.mq.captiway.scheduler.dao;

import org.hibernate.*;
import org.mq.captiway.scheduler.beans.CampaignReport;
import org.mq.captiway.scheduler.beans.CampaignSent;
import org.mq.captiway.scheduler.beans.Campaigns;
import org.mq.captiway.scheduler.beans.Contacts;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.exception.BaseDAOException;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.WebApplicationContextUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.text.*;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.orm.hibernate3.*; 

@SuppressWarnings({ "unchecked", "serial","unused" })
public class CampaignReportDaoForDML extends AbstractSpringDaoForDML {

    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
 

	public CampaignReportDaoForDML() {}
 	
	private JdbcTemplate jdbcTemplate;
	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}
	
	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
   /* public CampaignReport find(Long id) {
        return (CampaignReport) super.find(CampaignReport.class, id);
    }*/

    public void saveOrUpdate(CampaignReport campaignReport) {
        super.saveOrUpdate(campaignReport);
    }

    public void delete(CampaignReport campaignReport) {
        super.delete(campaignReport);
    }

    /*public List findAll() {
        return super.findAll(CampaignReport.class);
    }*/
    
   /* public void deleteByCollection(Collection campaignReportCollection){
    	getHibernateTemplate().deleteAll(campaignReportCollection);
    }*/
    
    public int updateReport(Long crId,String type,int num){
    	return jdbcTemplate.update("update campaign_report set  " + type + " = " + type + "+"+num+" where cr_id = "+crId);
     }
    
    public int updateCampaignReport(Long crId, String type) {
    	try {
		    	String queryStr = null;
		    	if(type.equals(Constants.CR_TYPE_OPENS))
		    		queryStr = "update CampaignReport set " + Constants.CR_TYPE_OPENS + 
		    			" = (SELECT count(sentId) FROM CampaignSent WHERE campaignReport =" + crId +
		    			" AND " + Constants.CS_TYPE_OPENS + " > 0) WHERE crId =" + crId;
		    	
		    	else if(type.equals(Constants.CR_TYPE_CLICKS))
		    		queryStr = "update CampaignReport set " + Constants.CR_TYPE_CLICKS + 
		    		" = (SELECT count(sentId) FROM CampaignSent WHERE campaignReport =" + crId + 
		    		" AND " + Constants.CS_TYPE_CLICKS + " > 0) WHERE crId =" + crId;
		    	
		    	//TODO: get bounce count from bounces table
		    	else if(type.equals(Constants.CR_TYPE_BOUNCES)) 
		    		queryStr = "update CampaignReport set " + Constants.CR_TYPE_BOUNCES + 
		    		" = (SELECT count(sentId) FROM CampaignSent WHERE campaignReport =" + crId + 
		    		" AND " + Constants.CS_TYPE_STATUS + " = '" + Constants.CS_STATUS_BOUNCED +
		    		"') WHERE crId =" + crId;
		
		    	else if(type.equals(Constants.CR_TYPE_SPAM))
		    		queryStr = "update CampaignReport set " + Constants.CR_TYPE_SPAM + 
		    		" = (SELECT count(sentId) FROM CampaignSent WHERE campaignReport =" + crId + 
		    		" AND " + Constants.CS_TYPE_STATUS + " = '" + Constants.CS_STATUS_SPAMMED +
		    		"') WHERE crId =" + crId;
		
		    	//TODO: get Unsubs from unsubscribes tables
		    	else if(type.equals(Constants.CR_TYPE_UNSUBSCRIBES) || type.equalsIgnoreCase("resubscribe"))
		    		queryStr = "update CampaignReport set " + Constants.CR_TYPE_UNSUBSCRIBES + 
		    		" = (SELECT count(sentId) FROM CampaignSent WHERE campaignReport =" + crId + 
		    		" AND " + Constants.CS_TYPE_STATUS+ " = '" + Constants.CS_STATUS_UNSUBSCRIBED +
		    		"') WHERE crId =" + crId;
		
		    	return ( (queryStr == null)? 0 : executeUpdate(queryStr) );
    	} catch(Exception e) {
    		if(logger.isErrorEnabled()) logger.error("** Exception while updateing the reports : crId : "+crId, e);

    		return 0;
    	}
    }
    
    
    public int updateCampaignBounceReport(Long crId, String type) {
    	
    	
    	String queryStr = null;
    	//TODO: get bounce count from bounces table
    	if(type.equals(Constants.CR_TYPE_BOUNCES)) 
    		queryStr = "update CampaignReport set " + Constants.CR_TYPE_BOUNCES + 
    		" = (SELECT count(sentId) FROM CampaignSent WHERE campaignReport =" + crId + 
    		" AND " + Constants.CS_TYPE_STATUS + " = '" + Constants.CS_STATUS_BOUNCED +
    		"') WHERE crId =" + crId;

    	else if(type.equals(Constants.CR_TYPE_SPAM))
    		queryStr = "update CampaignReport set " + Constants.CR_TYPE_SPAM + 
    		" = (SELECT count(sentId) FROM CampaignSent WHERE campaignReport =" + crId + 
    		" AND " + Constants.CS_TYPE_STATUS + " = '" + Constants.CS_STATUS_SPAMMED +
    		"') WHERE crId =" + crId;
    	
    	return ( (queryStr == null)? 0 : executeUpdate(queryStr) );
    	
    }
    	
		    
    
    
    public int updateCampaignReport(String crId, String sentId, String type) {
    	try {
		    	String queryStr = null;
		    	if(type.equals(Constants.CR_TYPE_OPENS))
		    		queryStr = "update CampaignReport set " + Constants.CR_TYPE_OPENS + 
		    			" = (SELECT count(sentId) FROM CampaignSent WHERE campaignReport =" + crId +
		    			" AND " + Constants.CS_TYPE_OPENS + " > 0) WHERE crId =" + crId;
		    	
		    	else if(type.equals(Constants.CR_TYPE_CLICKS))
		    		queryStr = "update CampaignReport set " + Constants.CR_TYPE_CLICKS + 
		    		" = (SELECT count(sentId) FROM CampaignSent WHERE campaignReport =" + crId + 
		    		" AND " + Constants.CS_TYPE_CLICKS + " > 0) WHERE crId =" + crId;
		    	
		    	//TODO: get bounce count from bounces table
		    	else if(type.equals(Constants.CR_TYPE_BOUNCES)) 
		    		queryStr = "update CampaignReport set " + Constants.CR_TYPE_BOUNCES + 
		    		" = (SELECT count(sentId) FROM CampaignSent WHERE campaignReport =" + crId + 
		    		" AND " + Constants.CS_TYPE_STATUS + " = '" + Constants.CS_STATUS_BOUNCED +
		    		"') WHERE crId =" + crId;
		
		    	else if(type.equals(Constants.CR_TYPE_SPAM))
		    		queryStr = "update CampaignReport set " + Constants.CR_TYPE_SPAM + 
		    		" = (SELECT count(sentId) FROM CampaignSent WHERE campaignReport =" + crId + 
		    		" AND " + Constants.CS_TYPE_STATUS + " = '" + Constants.CS_STATUS_SPAMMED +
		    		"') WHERE crId =" + crId;
		
		    	//TODO: get Unsubs from unsubscribes tables
		    	else if(type.equals(Constants.CR_TYPE_UNSUBSCRIBES) || type.equalsIgnoreCase("resubscribe"))
		    		queryStr = "update CampaignReport set " + Constants.CR_TYPE_UNSUBSCRIBES + 
		    		" = (SELECT count(sentId) FROM CampaignSent WHERE campaignReport =" + crId + 
		    		" AND " + Constants.CS_TYPE_STATUS+ " = '" + Constants.CS_STATUS_UNSUBSCRIBED +
		    		"') WHERE crId =" + crId;
		
		    	return ( (queryStr == null)? 0 : executeUpdate(queryStr) );
    	} catch(Exception e) {
    		if(logger.isErrorEnabled()) logger.error("** Exception while updateing the reports : crId : "+crId+ " , SentId : "+sentId, e);
    		return 0;
    	}
    }
    
    /*public CampaignReport findById(Long crId) {
    	
    	
    	String qry = "FROM CampaignReport WHERE crId="+crId.longValue();
    	
    	
    	List<CampaignReport> CampaignReportList = getHibernateTemplate().find(qry);
    	return (CampaignReportList.get(0));
    	
    	
    	
    	
    }*/
    
    
    
    
    
    
    
    
    
    
    
    
    
    /*public Long getUserIdByCrId(Long crId) {
    	
    	try {
			return jdbcTemplate.queryForLong("SELECT user_id FROM campaign_report WHERE cr_id="+crId);
		} catch (DataAccessException e) {
			if(logger.isErrorEnabled()) logger.error("** Exception while getting the userId for crId"+crId, e);
			return null;
		}
    }*/

/**
 * Added for EventTrigger
 * 
 * @param fromSourceWithTriggertype
 * @param userId
 * @return
 * 
 * Gets all the previous campaign reports for a givem triggertype. Ex: "EventTrigger_Opens.." etc
 */
   /*public List<Long> getCrIdsBySourceType(String fromSourceWithTriggertype,Long userId) {
    	
	   try {
        	String queryStr= " SELECT crId FROM CampaignReport " +
        			" WHERE sourceType LIKE '"+fromSourceWithTriggertype+"' "+
        			" AND user = " +userId+ " "; 

        	return getHibernateTemplate().find(queryStr);
    	}
    	catch(DataAccessException e) {
    		if(logger.isErrorEnabled()) logger.error("***Exception while getting Campaign Reports by Source Type****"+e);
    		return null;	
    	}
    	
    }*/ //getCrIdsBySourceType
   
   /**
    * 
    * @param campaignOrTriggerName
    * @param fromSourceWithTriggertype
    * @param userId
    * @return
    * 
    * This method is added for Event Trigger
    * 
    * Its used to filter the records based on the option selected by the user ET_FILTER_BY_TRIGGER_TYPE_FLAG
    *  1. filter by trigger type (no trigger name will be included in the query)
    *  2. filter by trigger Name....All the records which are entered 
    *  after the current trigger  
    *   
    *  In either cases Trigger Type condition is must in order to handle the cases where  
    *  trigger type might be modified for a given Trigger name.
    *  
    */
  /* public List<Long> getCrIdsByTriggerTypeOrName(String triggerName,
		   String fromSourceWithTriggertype, Long userId,Long id) { // added for EventTrigger
	   
	   try {

		   if(triggerName.isEmpty()){
				
			   if(logger.isDebugEnabled()) logger.debug("trigger created date or trigger name is empty ");
			   return null;
		   }

		   String queryStr= " SELECT cr.crId FROM CampaignReport cr, EventTrigger et " +
		   " WHERE et.id = "+id +" "+
		   " AND cr.user = " +userId+ " " +
		   " AND cr.campaignName LIKE '"+triggerName+"' "+
		   " AND cr.sourceType LIKE '"+fromSourceWithTriggertype+"' "+ 
		   " AND cr.sentDate > et.triggerCreatedDate";
		   
		   if(logger.isDebugEnabled()) logger.debug("getCrIdsByTriggerTypeOrName returning with query str "+queryStr);
		   return getHibernateTemplate().find(queryStr);
	   }
	   catch(DataAccessException e) {
		   if(logger.isErrorEnabled()) logger.error("***Exception while getting Campaign Reports by Campaign or Trigger Name ****"+e);
		   return null;	
	   }
   }*///getCrIdsByTriggerTypeOrName
   
   /**
    * Added for EventTrigger
    * 
    * @param campaignName
    * @param userId
    * @param opensFlag
    * @return
    * 
    * This method returns campaignReport of all the contacts who has opened/clicked the given campaignName
    * if opensFlag is set it is a open criteria else 'clicks' criteria.
    *  
    */
  /* public List<Long> getOpensClicksCrIds(String campaignName, Long userId,boolean opensFlag) { //added for EventTrigger
	   try {

		   if(campaignName.isEmpty()){
				
			   if(logger.isDebugEnabled()) logger.debug("trigger created date or trigger name is empty ");
			   return null;
		   }

		   String queryStr= " SELECT cr.crId FROM CampaignReport cr, CampaignSent cs" +
		   " WHERE cr.campaignName = '"+campaignName+"' " +
		   " AND cr.user = " +userId+ " " +
		   " AND cs.campaignReport = cr.crId ";
		   
		   if(opensFlag) {
			  queryStr += " AND cs.opens > 0";
		   }
		   else {
			   
			   queryStr +=" AND cs.clicks > 0";
		   }
		   		   
		   if(logger.isDebugEnabled()) logger.debug("getOpensClicksCrIds executing query "+queryStr);
		   return getHibernateTemplate().find(queryStr);
	   }
	   catch(DataAccessException e) {
		   if(logger.isErrorEnabled()) logger.error("***Exception while getting Campaign Reports by Campaign for opens ****"+e);
		   return null;	
	   }
   }*///getOpensCrIds
   /**
    * added for Auto Program
    * 
    * @param programName
    * @param activityCampWinIds
    * @return
    * 
    * 
    * this method return the crid s based on the given 'programName' and substring of sourcetype(activityCampWinIds) 
    * when activity filter is applied for the switch component. 
    */
   /*public String getCridsForSwitchActivityFilter(String programName, String activityCampWinIds,Calendar repConsDate) {
	   
	   
	   Date reportConsDate = repConsDate.getTime();
	   String reportConsFormattedDate = format.format(reportConsDate);
	   
	   String qry = "SELECT cr_id FROM campaign_report where campaign_name='" +programName+"' " +
	   				"AND SUBSTRING(source_type,18) in ("+activityCampWinIds+") AND sent_date >= '"+reportConsFormattedDate+"'";
		String crIds = "";
		List<Long> crIdList = jdbcTemplate.queryForList(qry, Long.class);
		if(crIdList.isEmpty()) {
			
			if(logger.isDebugEnabled()) logger.debug("no campaign reports found for this query"+qry);
			return "";
		}
		
		for(Long id : crIdList) {
			
			if(crIds.length() > 0 ) crIds += ",";
			crIds += id;
		}
		return crIds;
	   
   }*/
   
   /*public CampaignReport findBy(Long userID, String campaignName, boolean isFirst) throws BaseDAOException{
	   //String name = StringEscapeUtils.escapeSql(campaignName);
	   String qry = "FROM CampaignReport WHERE user ="+userID.longValue()+" AND campaignName='"+campaignName+"' ORDER BY 1 "+(!isFirst ? "DESC" : "");
	   
	   List<CampaignReport> retList = executeQuery(qry, 0, 1);
	   
	   if(retList != null && retList.size()>0) return retList.get(0);
	   
	   return null;
	   
   }
   
   public List<CampaignReport> getWeeklyCampaignReports(Long userId, String crId) {
		
		String checkCon = "";
		
			
			String queryStr = " FROM CampaignReport WHERE user =" + userId + " and status = 'sent' and crId IN ("+ crId + ")";
			logger.debug("Query : " + queryStr);
		
			return getHibernateTemplate().find(queryStr);
	}*/
   
   
}//CampaignReportDao
