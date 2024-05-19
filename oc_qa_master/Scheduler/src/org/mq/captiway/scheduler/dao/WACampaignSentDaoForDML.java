package org.mq.captiway.scheduler.dao;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.WACampaignSent;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.optculture.exception.BaseServiceException;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;

public class WACampaignSentDaoForDML extends AbstractSpringDaoForDML {
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private JdbcTemplate jdbcTemplate;

	public WACampaignSentDaoForDML() {
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public void saveOrUpdate(WACampaignSent waCampaignSent) {
		super.saveOrUpdate(waCampaignSent);
	}


	public void delete(WACampaignSent waCampaignSent) {
		super.delete(waCampaignSent);
	}

	public void saveByCollection(Collection<WACampaignSent> campList) {
		super.saveOrUpdateAll(campList);
	}

	public int updateStatusBySentId(String status, String mobile, Long sentId) {
		if(mobile.length() == 12) {
			mobile = mobile.substring(2);
		}

		String queryStr = "update WACampaignSent set status ='"+status+"' where sentId="+sentId ; //+" and mobileNumber like '%"+mobile+"'";
		int updateCount = executeUpdate(queryStr);
		return updateCount;
	}

	public int updateApiMsgId(String actualSentId, Long waCampRepId, String apiMsgId) {

		String updateQry;
		int count=0 ;
		try {
			updateQry = " UPDATE WACampaignSent SET apiMsgId='"+apiMsgId+"' WHERE "
		                  //+(waCampRepId == null ? "" :	"waCampaignReport.waCrId="+waCampRepId+" AND ")
		                  +" sentId="+actualSentId;

			logger.debug(updateQry);
			count = executeUpdate(updateQry);
		} catch (Exception e) {
			logger.error("Exception :",e);
		}		
		return count;		

	}

	/*public int updateClicks(Long sentId, Long waCrID) throws BaseServiceException{
	
		try {
			String queryStr = "UPDATE wa_campaign_sent cs " +
					" JOIN (SELECT count(click_id) as cnt, sent_id as sent_id FROM wa_clicks "+
					" WHERE sent_id IN("+sentId.longValue()+")  GROUP BY sent_id) o " +
					" ON cs.sent_id = o.sent_id" +
					"	SET cs.clicks=o.cnt,cs.status='"+Constants.WA_SENT_STATUS_DELIVERED+"' WHERE cs.wa_cr_id="+waCrID.longValue();
			logger.info("update clicks queryStr :"+queryStr);
			return jdbcTemplate.update(queryStr);
		} catch (DataAccessException e) {
			throw new BaseServiceException("Exception while updating the report");
		}
	
	}*///updateClicks
	
	public int updateStatusBySentId(String status, long sentId) {
		
//		String sub_qry = "";
//		if(apiMsgId!=null) sub_qry=" AND (apiMsgId= case  when apiMsgId IS  NOT NULL then '"+apiMsgId+"' END )";

		
		String queryStr = "update WACampaignSent set status ='"+status+"' where  sentId="+sentId;
				//+" mobileNumber like '%"+mobile+"' AND "
				//+ sub_qry;
		logger.debug("update status query : "+queryStr);
		int updateCount = executeUpdate(queryStr);
		
		return updateCount;
	}//updateStatusBySentId
	
	public int updateStatusByMsgId(String status, String apiMsgId) {
		
		
		String queryStr = "update WACampaignSent set status ='"+status+"' where  apiMsgId='"+apiMsgId+"'";
		
		logger.debug("update status query : "+queryStr);
		int updateCount = executeUpdate(queryStr);
		
		return updateCount;
	}//updateStatusByMsgId
	
	/**
	 * update open=1 when msg is read
	 */
	public int updateOpensBySendId(long sentId) {
		
//		String sub_qry = "";
//		if(apiMsgId!=null) sub_qry=" AND (apiMsgId= case  when apiMsgId IS  NOT NULL then '"+apiMsgId+"' END )";

		
		String queryStr = "update WACampaignSent set opens = opens+1 where  sentId="+sentId;
							//+ sub_qry;
		logger.debug("update status query : "+queryStr);
		int updateCount = executeUpdate(queryStr);
		
		return updateCount;
	}//updateOpensBySendId
	
	public int updateOpensByAPIMsgId(String apiMsgId) {
		
		String queryStr = "update WACampaignSent set opens = opens+1 where  apiMsgId='"+apiMsgId+"'";
		
		logger.debug("update status query : "+queryStr);
		int updateCount = executeUpdate(queryStr);
		
		return updateCount;
	}//updateOpensByAPIMsgId

}
