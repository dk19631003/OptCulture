package org.mq.captiway.scheduler.dao;

import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.SMSCampaignSent;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.utility.SMSStatusCodes;
import org.mq.optculture.exception.BaseDAOException;
import org.mq.optculture.exception.BaseServiceException;
import org.mq.optculture.utils.OCConstants;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class SMSCampaignSentDao extends AbstractSpringDao {
	SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private JdbcTemplate jdbcTemplate;
	
	private Long currentSentId;

	public SMSCampaignSentDao() {
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public SMSCampaignSent find(Long id) {
		return (SMSCampaignSent) super.find(SMSCampaignSent.class, id);
	}

	/*public void saveOrUpdate(SMSCampaignSent smsCampaignSent) {
		super.saveOrUpdate(smsCampaignSent);
	}*/

	/*public void saveOrUpdateAll(Collection<SMSCampaignSent> smsCampSentList){
		super.saveOrUpdateAll(smsCampSentList);
	}*/
	/*
	public void delete(SMSCampaignSent smsCampaignSent) {
		super.delete(smsCampaignSent);
	}
*/
	public List findAll() {
		return super.findAll(SMSCampaignSent.class);
	}

	/*private OpensDao opensDao;

	public void setOpensDao(OpensDao opensDao) {
		this.opensDao = opensDao;
	}

	public OpensDao getOpensDao() {
		return this.opensDao;
	}
*//*
	public void saveByCollection(Collection<SMSCampaignSent> campList) {
		super.saveOrUpdateAll(campList);
	}*/
	
	public synchronized Long getCurrentSentId() {
		
		try {
			Long currentSentId = null;
			 currentSentId =  (Long)getHibernateTemplate().find("SELECT MAX(sentId) FROM SMSCampaignSent ").get(0);
			if(logger.isDebugEnabled()) logger.debug("current sent id in dao is=====>"+currentSentId);
			if(currentSentId == null) {
				currentSentId = 0l;
			}
			this.currentSentId = currentSentId;
			return currentSentId;
		} catch (Exception e) {
			if(logger.isErrorEnabled()) logger.error(" ** Exception : while getting the current sentId from the Database so returning" +
					"currentSentId value as -"+this.currentSentId+100000, e);
			return this.currentSentId+100000;
		}
		
	}
	
	/*public long getSentCount(Long smsCrId) {
		String qry = "select count(sentId) from SMSCampaignSent where smsCampaignReport="+smsCrId;
		try{
			return ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		}catch (Exception e) {
			// TODO: handle exception
			if(logger.isErrorEnabled()) logger.error("Exception",e);
			return 0;
		}
	}*/
	
	//harshi
	public long getSentCount(Long smsCrId) {
		String qry = "select count(sentId) from SMSCampaignSent where smsCampaignReport="+smsCrId+ "AND status NOT IN ('"+OCConstants.SMS_NOT_SUBMITTED+"')";
		try{
			return ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		}catch (Exception e) {
			// TODO: handle exception
			if(logger.isErrorEnabled()) logger.error("Exception",e);
			return 0;
		}
	}
	/*
	public void updateInitialStatus(String sentId, String status) {
		
		String queryStr = "update SMSCampaignSent set status ='"+status+"' where sentId="+sentId;
		executeUpdate(queryStr);
		
	}
	public void updateInitialStatusToMultiple(Long sentId, Long endSentId, String status) {
		
		String queryStr = "update SMSCampaignSent set status ='"+status+"' where sentId BETWEEN "+sentId+" AND "+endSentId;
		executeUpdate(queryStr);
		
	}
	*/
	
	/*public int updateInitialStatusToMultipleOutreach(Set<Long> outreachSentIdsSet, String status) {
		
		 try {
			 	String sentIdsStr = Constants.STRING_NILL;
			 	
			 	for(Long aSentId : outreachSentIdsSet){
			 		if(sentIdsStr.length() > 0) sentIdsStr += Constants.DELIMETER_COMMA;
					sentIdsStr += aSentId;
			 	}
			 	String queryStr = "UPDATE sms_campaign_sent SET status='"+status+"' WHERE sent_id IN ("+sentIdsStr+") ";
				logger.info("queryStrOutreach >>> "+queryStr);
			 	
				return jdbcTemplate.update(queryStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception >>> ",e);
				return 0;
			}
		
	}*/
	
	public List<SMSCampaignSent> findBy_Outreach(Set<Long> outreachSentIdsSet, Long camprepId) {
		try{
			String sentIdsStr = Constants.STRING_NILL;

			for(Long aSentId : outreachSentIdsSet){
				if(sentIdsStr.length() > 0) sentIdsStr += Constants.DELIMETER_COMMA;
				sentIdsStr += aSentId;
			}

			List<SMSCampaignSent> smsList =  executeQuery("FROM SMSCampaignSent WHERE "
					+ " smsCampaignReport="+camprepId+" AND sentId IN ("+sentIdsStr+") ");

			if(smsList != null && smsList.size() > 0){

				return smsList;
			}
		}catch(Exception e){
			logger.error("Exception >>> ",e);
		}
		return null;
		
		
	}
	
	
	public List<SMSCampaignSent> findBy_BsmsItsPak(Set<Long> bsmsItsSentIdsSet, Long camprepId) {
		try{
			String sentIdsStr = Constants.STRING_NILL;

			for(Long aSentId : bsmsItsSentIdsSet){
				if(sentIdsStr.length() > 0) sentIdsStr += Constants.DELIMETER_COMMA;
				sentIdsStr += aSentId;
			}

			List<SMSCampaignSent> smsList =  executeQuery("FROM SMSCampaignSent WHERE "
					+ " smsCampaignReport="+camprepId+" AND sentId IN ("+sentIdsStr+") ");

			if(smsList != null && smsList.size() > 0){

				return smsList;
			}
		}catch(Exception e){
			logger.error("Exception >>> ",e);
		}
		return null;
		
		
	}
	
	
	/*public int updateStatus(String status, String mobile, String reqId) {
		if(mobile.length() == 12) {
			
			
			mobile = mobile.substring(2);
		}
		
		
		
			String queryStr = "update SMSCampaignSent set status ='"+status+"' where requestId='"+reqId+"' and mobileNumber like '%"+mobile+"'";
			int updateCount = executeUpdate(queryStr);
			return updateCount;
	}*/
	
	/*public int updateStatusByCliMsgId(String status, String mobile, long cliMsgId, String apiMsgId) {
		if(mobile.length() == 12) {
			
			
			mobile = mobile.substring(2);
		}
		*/
		
		/*"update SMSCampaignSent s set s.status='ddsfdfdfsdfdsxcxzcf23233',api_msg_id=  'ghghg'" +
		" where  s.sent_id=512 and s.mobile_number like '%919848495956' and ( s.api_msg_id=case  when s.api_msg_id IS  NOT NULL then  'ghghg' end OR s.api_msg_id IS NULL)
*/		/*String queryStr = "update SMSCampaignSent set status ='"+status+"',apiMsgId='"+apiMsgId+"' where sentId="+cliMsgId+" " +
				"and mobileNumber like '%"+mobile+"' AND (apiMsgId= case  when apiMsgId IS  NOT NULL then '"+apiMsgId+"' END OR apiMsgId IS NULL)";
			//String queryStr = "update SMSCampaignSent set status ='"+status+"' where sentId="+cliMsgId+" AND apiMsgId='"+apiMsgId+"' and mobileNumber like '%"+mobile+"'";
			int updateCount = executeUpdate(queryStr);
			return updateCount;
			
	}*/
	
	
	public SMSCampaignSent findBySentId(String id, Long camprepId) {
		
		List<SMSCampaignSent> smsList =  getHibernateTemplate().find("from SMSCampaignSent WHERE smsCampaignReport="+camprepId+" AND sentId="+id);
		
		if(smsList != null && smsList.size() > 0){
			
			return smsList.get(0);
		}
		return null;
		
		
	}
	
	public List<SMSCampaignSent> findBy(Long id, Long endSentId, Long camprepId) {
		
		List<SMSCampaignSent> smsList =  executeQuery("FROM SMSCampaignSent WHERE "
				+ " smsCampaignReport="+camprepId+" AND sentId BETWEEN "+id+" AND "+endSentId);
		
		if(smsList != null && smsList.size() > 0){
			
			return smsList;
		}
		return null;
		
		
	}
	
	
	
	
	
	public SMSCampaignSent findByReqIdAndMobile(String clientMsgId, String mobile) {//query need to be changed other wise confirm the type of mobile numbers.
		
		if(mobile.length() == 12) {
			
			
			mobile = mobile.substring(2);
		}
		
		List<SMSCampaignSent> retSmsCampaignSentList = getHibernateTemplate().find("FROM SMSCampaignSent " +
											"Where sentId="+clientMsgId+" AND mobileNumber like '%"+mobile+"'");
		
		if(retSmsCampaignSentList != null && retSmsCampaignSentList.size() > 0) {
			
			return retSmsCampaignSentList.get(0);
		}
		
		return null;
		
		
	}
	
	public SMSCampaignSent findByReqIdAndMobile(long clientMsgId, String mobile) {//query need to be changed other wise confirm the type of mobile numbers.
		
		if(mobile.length() == 12) {
			
			
			mobile = mobile.substring(2);
		}
		
		List<SMSCampaignSent> retSmsCampaignSentList = getHibernateTemplate().find("FROM SMSCampaignSent " +
											"Where sentId="+clientMsgId+" AND mobileNumber like '%"+mobile+"'");
		
		if(retSmsCampaignSentList != null && retSmsCampaignSentList.size() > 0) {
			
			return retSmsCampaignSentList.get(0);
		}
		
		return null;
		
		
	}
	
	/*
	 * smsCampaignSentDao.getDeliveredCount(smsDeliveryReport.getSmsCampRepId(), 
											smsDeliveryReport.getRequestId(), "Delivered");
	 */
	/**
	 * 
	 */
	public int getDeliveredCount(Long repId, String requestId, String status) {
		
		return ((Long)getHibernateTemplate().find("select count(sentId) from SMSCampaignSent where smsCampaignReport="+
					repId+"and requestId='"+requestId+"' and status='"+status+"'").get(0)).intValue();
		
	}
	
	/*public int updateApiMsgId(String actualSentId, Long smsCampRepId, String apiMsgId) {
		
		String updateQry = " UPDATE SMSCampaignSent SET apiMsgId='"+apiMsgId+"' WHERE "+(smsCampRepId == null ? "" :
			"smsCampaignReport="+smsCampRepId+" AND ")+" sentId="+actualSentId;
		
		logger.debug(updateQry);
		return executeUpdate(updateQry);
		
	}*/
	
	public List<SMSCampaignSent> findByIds(String sentIds) {
		
		List<SMSCampaignSent> retSmsCampaignSentList = getHibernateTemplate().find("FROM SMSCampaignSent " +
				"WHERE sentId IN("+sentIds+") ORDER BY smsCampaignReport.smsCrId");

		if(retSmsCampaignSentList != null && retSmsCampaignSentList.size() > 0) {
		
			return retSmsCampaignSentList;
		}

		return null;
		
		
	}
	
public List<SMSCampaignSent> findByMsgIds(String msgIds) {
		
		List<SMSCampaignSent> retSmsCampaignSentList = getHibernateTemplate().find("FROM SMSCampaignSent " +
				"WHERE apiMsgId IN("+msgIds+") ORDER BY smsCampaignReport.smsCrId");

		if(retSmsCampaignSentList != null && retSmsCampaignSentList.size() > 0) {
		
			return retSmsCampaignSentList;
		}

		return null;
		
		
	}
	
	public List<SMSCampaignSent> findByRepId(Long smsCampRepId, String status, int startFrom, int count) {
		
	/*	List<SMSCampaignSent> retSmsCampaignSentList = getHibernateTemplate().find("FROM SMSCampaignSent " +
				" WHERE smsCampaignReport="+smsCampRepId+" AND status='"+status+"'");*/
		
		String queryStr = "FROM SMSCampaignSent " +
							" WHERE smsCampaignReport="+smsCampRepId+" AND status IN("+status+")";
		
		List<SMSCampaignSent> retSmsCampaignSentList = executeQuery(queryStr, startFrom, count);

		if(retSmsCampaignSentList != null && retSmsCampaignSentList.size() > 0) {
		
			return retSmsCampaignSentList;
		}

		return null;
		
		
	}
	
	 public int getCount(Long smsCampRepId, String status) {
	    	
	    	String qry = "SELECT count(sentId) FROM SMSCampaignSent where smsCampaignReport="+smsCampRepId+" AND status IN("+status+")";
			int count = ((Long)getHibernateTemplate().find(qry).get(0)).intValue();	
			return count;
	    	
	    	
	    	
	    	
	    }
	 
	 /*public int updateClicks(Long sentId, Long smsCrID) throws BaseServiceException{
		 
		 try {
			String queryStr = "UPDATE sms_campaign_sent cs " +
						" JOIN (SELECT count(click_id) as cnt, sent_id as sent_id FROM sms_clicks "+
						" WHERE sent_id IN("+sentId.longValue()+")  GROUP BY sent_id) o " +
						" ON cs.sent_id = o.sent_id" +
						"	SET cs.clicks=o.cnt,cs.status='"+SMSStatusCodes.CLICKATELL_STATUS_RECEIVED+"' WHERE cs.sms_cr_id="+smsCrID.longValue();
			 
				return jdbcTemplate.update(queryStr);
		} catch (DataAccessException e) {
			// TODO Auto-generated catch block
			throw new BaseServiceException("Exception while updating the report");
		}
				
	 }
	 */
	/* public int updateReqId(Long startSentId, Long endSentId, String requestId) throws BaseDAOException{
		 
		 String query = "update SMSCampaignSent set requestId='"+requestId+"' WHERE "
		 		+ "sentId BETWEEN "+startSentId+" AND "+endSentId;
		 
		 int updatedCount = executeUpdate(query);
		 
		 return updatedCount;
	 }
	 
	 public int updateReqId_Outreach(Set<Long> outreachSentIdsSet, String requestId){
		 
		 
		 try {
			 	String sentIdsStr = Constants.STRING_NILL;
			 	
			 	for(Long aSentId : outreachSentIdsSet){
			 		if(sentIdsStr.length() > 0) sentIdsStr += Constants.DELIMETER_COMMA;
					sentIdsStr += aSentId;
			 	}
			 	String queryStr = "UPDATE sms_campaign_sent SET request_id='"+requestId+"' WHERE sent_id IN ("+sentIdsStr+") ";
				logger.info("queryStrOutreach >>> "+queryStr);
			 	
				return jdbcTemplate.update(queryStr);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				logger.error("Exception >>> ",e);
				return 0;
			}
	 }*/
	 
	 public long getAllRequestIdsCountBy(Long smsCrId) throws BaseDAOException{
		 
		 String query = " SELECT COUNT(DISTINCT requestId) FROM SMSCampaignSent WHERE "
		 			+ " smsCampaignReport="+smsCrId.longValue()+" AND requestId IS NOT NULL AND status IN('"+
				 Constants.SMS_SENT_STATUS_STATUS_PENDING+"','"+SMSStatusCodes.MVAYOO_STATUS_PENDING+"')";
			 
		 List<Long> retList  = executeQuery(query);
		 
		 if(retList != null && retList.size() > 0) return retList.get(0).longValue();
		 
		 return 0;
		 
	 }
	 
	 public List<String> getAllRequestIdsBy(Long smsCrId, int startFrom, int size) throws BaseDAOException{
		 
		 String query = " SELECT DISTINCT requestId FROM SMSCampaignSent WHERE "
		 			+ " smsCampaignReport="+smsCrId.longValue()+" AND requestId IS NOT NULL  AND status IN('"+
				 Constants.SMS_SENT_STATUS_STATUS_PENDING+"','"+SMSStatusCodes.MVAYOO_STATUS_PENDING+"')";
			 
		List<String> retList = executeQuery(query, startFrom, size);
		 
		 return retList;
		 
	 }
	 
	 public long findCountBy(Long smsCrID, String tid) throws BaseDAOException{
		 
		 String query = " SELECT COUNT(sentId) FROM SMSCampaignSent WHERE "
		 			+ " smsCampaignReport="+smsCrID.longValue()+" AND requestId IS NOT NULL AND requestId IN("+tid+") AND status IN('"+
				 Constants.SMS_SENT_STATUS_STATUS_PENDING+"','"+SMSStatusCodes.MVAYOO_STATUS_PENDING+"')";
			 
		 List<Long> retList  = executeQuery(query);
		 
		 if(retList != null && retList.size() > 0) return retList.get(0).longValue();
		 
		 return 0;
		 
		 
	 }
	 
	 public List<SMSCampaignSent> findBy(Long smsCrID, String tid, int startFrom, int size) throws BaseDAOException{
		 
		 String query = "  FROM SMSCampaignSent WHERE "
		 			+ " smsCampaignReport="+smsCrID.longValue()+" AND requestId IS NOT NULL AND requestId IN("+tid+") AND status IN('"+
				 Constants.SMS_SENT_STATUS_STATUS_PENDING+"','"+SMSStatusCodes.MVAYOO_STATUS_PENDING+"')";
			 
		 List<SMSCampaignSent> retList  = executeQuery(query, startFrom, size);
		 
		 if(retList != null && retList.size() > 0) ;
		 return retList;
		 
		 
	 }

	public List<SMSCampaignSent> findBySentAndMsgIds(String sentIdsStr,
			String string) {
		List<SMSCampaignSent> retSmsCampaignSentList = getHibernateTemplate().find("FROM SMSCampaignSent " +
				"WHERE apiMsgId='' and sentId NOT IN("+string+") ORDER BY smsCampaignReport.smsCrId");

		if(retSmsCampaignSentList != null && retSmsCampaignSentList.size() > 0) {
		
			return retSmsCampaignSentList;
		}

		return null;
		
		
	}
public long findCountOfMsgsIdsBy(Long smsCrId) {
        
        String query = "SELECT COUNT(sentId) FROM SMSCampaignSent WHERE "
                + " smsCampaignReport="+smsCrId+" AND apiMsgId IS NOT NULL";
        
        List<Long> retList = executeQuery(query);
        try{
            return ((Long)retList.get(0)).longValue();
        }catch (Exception e) {
            // TODO: handle exception
            if(logger.isErrorEnabled()) logger.error("Exception",e);
            return 0;
        }
    }

public long findCountOfNotSubmitted(Long smsCrId) {
    
    String query = "SELECT COUNT(sentId) FROM SMSCampaignSent WHERE "
            + " smsCampaignReport="+smsCrId+" AND status='"+OCConstants.SMS_NOT_SUBMITTED+"'";
    
    List<Long> retList = executeQuery(query);
    try{
        return ((Long)retList.get(0)).longValue();
    }catch (Exception e) {
        // TODO: handle exception
        if(logger.isErrorEnabled()) logger.error("Exception",e);
        return 0;
    }
}
}
