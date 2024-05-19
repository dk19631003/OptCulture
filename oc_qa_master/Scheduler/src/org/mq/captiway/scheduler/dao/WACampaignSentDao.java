package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.mq.captiway.scheduler.beans.WACampaignSent;
import org.mq.captiway.scheduler.utility.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class WACampaignSentDao extends AbstractSpringDao {
	private static final  Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
	private JdbcTemplate jdbcTemplate;

	private Long currentSentId;

	public WACampaignSentDao() {
	}

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	public WACampaignSent find(Long id) {
		return (WACampaignSent) super.find(WACampaignSent.class, id);
	}

	public List findAll() {
		return super.findAll(WACampaignSent.class);
	}

	public synchronized Long getCurrentSentId() {

		try {
			Long currentSentId = null;
			currentSentId =  (Long)getHibernateTemplate().find("SELECT MAX(sentId) FROM WACampaignSent ").get(0);
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

	public long getSentCount(Long waCrId) {
		String qry = "select count(sentId) from WACampaignSent where waCampaignReport="+waCrId+ " AND status NOT IN ('"+Constants.WA_STATUS_NotSubmitted+"')";
		try{
			return ((Long)getHibernateTemplate().find(qry).get(0)).longValue();
		}catch (Exception e) {
			if(logger.isErrorEnabled()) logger.error("Exception",e);
			return 0;
		}
	}

	public int getDeliveredCount(Long repId, String requestId, String status) {

		return ((Long)getHibernateTemplate().find("select count(sentId) from WACampaignSent where waCampaignReport="+
				repId+"and requestId='"+requestId+"' and status='"+status+"'").get(0)).intValue();

	}

	public long findCountOfNotSubmitted(Long waCrId) {

		String query = "SELECT COUNT(sentId) FROM WACampaignSent WHERE "
				+ " waCampaignReport="+waCrId+" AND status='NotSubmitted'";

		List<Long> retList = executeQuery(query);
		try{
			return ((Long)retList.get(0)).longValue();
		}catch (Exception e) {
			if(logger.isErrorEnabled()) logger.error("Exception",e);
			return 0;
		}
	}//findCountOfNotSubmitted
	
	public WACampaignSent findByAPIMsgIdAndSentId(String apiMsgId,Long sentId) {
		
		String sub_qry = "";
		if(apiMsgId!=null) sub_qry=" AND (apiMsgId= case  when apiMsgId IS  NOT NULL then '"+apiMsgId+"' END )";
		
		List<WACampaignSent> retWaCampaignSentList = getHibernateTemplate().find("FROM WACampaignSent " +
											"Where sentId = "+sentId + sub_qry);
		
		if(retWaCampaignSentList != null && retWaCampaignSentList.size() > 0) {
			
			return retWaCampaignSentList.get(0);
		}
		
		return null;
	}//findByAPIMsgIdAndSentId
	
	public WACampaignSent findBySentId(long sentId) {
		
		List<WACampaignSent> retWaCampaignSentList = getHibernateTemplate().find("FROM WACampaignSent " +
											"Where sentId = "+sentId );
		
		if(retWaCampaignSentList != null && retWaCampaignSentList.size() > 0) {
			
			return retWaCampaignSentList.get(0);
		}
		
		return null;
	}//findBySentId
	
	public WACampaignSent findByAPIMsgId(String apiMsgId) {//wamid.HBgMOTE3MDczOTM1MjYzFQIAERgSMDhDMjg0NkM2QjA1MzU4QkE3AA==
		
		List<WACampaignSent> retWaCampaignSentList = getHibernateTemplate().find("FROM WACampaignSent " +
											"Where apiMsgId = '"+apiMsgId+"'" );
		
		if(retWaCampaignSentList != null && retWaCampaignSentList.size() > 0) {
			
			return retWaCampaignSentList.get(0);
		}
		
		return null;
	}//findByAPIMsgId
}
