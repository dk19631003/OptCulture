package org.mq.captiway.scheduler.dao;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.CampaignReport;
import org.mq.captiway.scheduler.beans.SMSCampaignReport;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.exception.BaseServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class SMSCampaignReportDao extends AbstractSpringDao {
	 SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	 

		public SMSCampaignReportDao() {}
	 	
		private JdbcTemplate jdbcTemplate;
		public JdbcTemplate getJdbcTemplate() {
			return jdbcTemplate;
		}
		
		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}
		
	    public SMSCampaignReport find(Long id) {
	        return (SMSCampaignReport) super.find(SMSCampaignReport.class, id);
	    }

	  /*  public void saveOrUpdate(SMSCampaignReport smsCampaignReport) {
	        super.saveOrUpdate(smsCampaignReport);
	    }

	    public void delete(SMSCampaignReport smsCampaignReport) {
	        super.delete(smsCampaignReport);
	    }*/

	    public List findAll() {
	        return super.findAll(SMSCampaignReport.class);
	    }
	    
	   /* public void deleteByCollection(Collection smsCampaignReportCollection){
	    	getHibernateTemplate().deleteAll(smsCampaignReportCollection);
	    }
	    
	    public int updateReport(Long crId,String type,int num){
	    	return jdbcTemplate.update("update sms_campaign_report set  " + type + " = " + type + "+"+num+" where cr_id = "+crId);
	     }*/
	    
	    public SMSCampaignReport findByRepId(Long repId){
	    	
	    	return (SMSCampaignReport)getHibernateTemplate().find("from SMSCampaignReport where smsCrId = "+repId).get(0);
	    	
	    }
	    
	    /**
	     * added for EventTrigger sms feature
	     * this method is similar to campaignReportDao.getCrIdsByTriggerTypeOrName
	     * 
	     * @param triggerName
	     * @param fromSourceWithTriggertype
	     * @param userId
	     * @param id
	     * @return
	     */
	    public List<Long> getCrIdsByTriggerTypeOrName(String smsName,
	 		   String fromSourceWithTriggertype, Long userId,Long id) { // added for EventTrigger
	 	   
	 	   try {

	 		   if(smsName.isEmpty()){
	 				
	 			   if(logger.isDebugEnabled()) logger.debug("trigger created date or trigger name is empty ");
	 			   return null;
	 		   }

	 		   String queryStr= " SELECT scr.smsCrId FROM SMSCampaignReport scr, SMSCampaigns sc " +
	 		   " WHERE sc.smsCampaignId = "+id +" "+
	 		   " AND scr.user = " +userId+ " " +
	 		   " AND scr.smsCampaignName LIKE '"+smsName+"' "+
	 		   " AND scr.sourceType LIKE '"+fromSourceWithTriggertype+"' "+ 
	 		   " AND scr.sentDate > sc.createdDate";
	 		   
	 		   if(logger.isDebugEnabled()) logger.debug("getCrIdsByTriggerTypeOrName returning with query str "+queryStr);
	 		   return getHibernateTemplate().find(queryStr);
	 	   }
	 	   catch(DataAccessException e) {
	 		   if(logger.isErrorEnabled()) logger.error("***Exception while getting Campaign Reports by Campaign or Trigger Name ****"+e);
	 		   return null;	
	 	   }
	    }//getCrIdsByTriggerTypeOrName
	    

	   /* public int updateBounceReport(Long smsCrId) {
	    	
	    	try {
				String qry = " UPDATE sms_campaign_report set bounces=(bounces+1) WHERE sms_cr_id="+smsCrId.longValue();
				
				return jdbcTemplate.update(qry);
				
				String qry = "UPDATE sms_campaign_report cs " +
							" JOIN (SELECT count(sent_id) cnt,sms_cr_id FROM sms_campaign_sent" +
				    				" WHERE sms_cr_id =" + smsCrId.longValue() + " AND " + Constants.CS_TYPE_STATUS + " = '" + Constants.CS_STATUS_BOUNCED +
				    				"') o " +
							" ON cs.sms_cr_id = o.sms_cr_id" +
							"	SET cs.bounces=o.cnt where cs.sms_cr_id="+smsCrId.longValue();

				return jdbcTemplate.update(qry);
				
				String qry = " UPDATE SMSCampaignReport SET bounces = (SELECT count(sentId) FROM SMSCampaignSent" +
							" WHERE smsCampaignReport =" + smsCrId.longValue() + " AND " + Constants.CS_TYPE_STATUS + " = '" + Constants.CS_STATUS_BOUNCED +
							"')  WHERE smsCrId ="+smsCrId.longValue();
				
				//return executeUpdate(qry);
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				logger.error("Exception ",e);
				return 0;
				
			}
	    	
	    	
	    	
	    }
	    
	   public int updateClickReport(Long smsCrId) throws BaseServiceException{
	    	

	    	
	    	try {
				String qry = " UPDATE sms_campaign_report set bounces=(bounces+1) WHERE sms_cr_id="+smsCrId.longValue();
				
				return jdbcTemplate.update(qry);
				
				String qry = "UPDATE sms_campaign_report cs " +
							" JOIN (SELECT count(sent_id) cnt,sms_cr_id FROM sms_campaign_sent" +
				    				" WHERE sms_cr_id =" + smsCrId.longValue() + " AND clicks > 0) o " +
							" ON cs.sms_cr_id = o.sms_cr_id" +
							"	SET cs.clicks=o.cnt where cs.sms_cr_id="+smsCrId.longValue();

				return jdbcTemplate.update(qry);
				
				String qry = " UPDATE SMSCampaignReport SET bounces = (SELECT count(sentId) FROM SMSCampaignSent" +
							" WHERE smsCampaignReport =" + smsCrId.longValue() + " AND " + Constants.CS_TYPE_STATUS + " = '" + Constants.CS_STATUS_BOUNCED +
							"')  WHERE smsCrId ="+smsCrId.longValue();
				
				//return executeUpdate(qry);
			} catch (DataAccessException e) {
				// TODO Auto-generated catch block
				throw new BaseServiceException("Exception in updating the report");
				
			}
	    	
	    	
	    	
	    
	    	
	    	
	    }*/

	    
	    /**
	     * Added for EventTrigger sms feature
	     * @param fromSourceWithTriggertype
	     * @param userId
	     * @return
	     * 
	     * Gets all the previous campaign reports for a givem triggertype. Ex: "EventTrigger_Opens.." etc
	     */
	       public List<Long> getCrIdsBySourceType(String fromSourceWithTriggertype,Long userId) {
	        	
	    	   try {
	            	String queryStr= " SELECT smsCrId FROM SMSCampaignReport " +
	            			" WHERE sourceType LIKE '"+fromSourceWithTriggertype+"' "+
	            			" AND user = " +userId+ " "; 

	            	return getHibernateTemplate().find(queryStr);
	        	}
	        	catch(DataAccessException e) {
	        		if(logger.isErrorEnabled()) logger.error("***Exception while getting Campaign Reports by Source Type****"+e);
	        		return null;	
	        	}
	        	
	        } //getCrIdsBySourceType

		public List<SMSCampaignReport> getWeeklySMSCampaignReports(Long userId, String crId) {
		
		String checkCon = "";
		
			
			String queryStr = " FROM SMSCampaignReport WHERE user =" + userId + " and status = 'sent' and smsCrId IN ("+ crId + ")";
			logger.debug("Query : " + queryStr);
		
			return getHibernateTemplate().find(queryStr);
		}



}
