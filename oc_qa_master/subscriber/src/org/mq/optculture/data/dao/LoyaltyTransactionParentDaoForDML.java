package org.mq.optculture.data.dao;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.LoyaltyProgramTrans;
import org.mq.marketer.campaign.beans.LoyaltyTransactionParent;
import org.mq.marketer.campaign.dao.AbstractSpringDao;
import org.mq.marketer.campaign.dao.AbstractSpringDaoForDML;
import org.mq.marketer.campaign.general.Constants;
import org.mq.optculture.exception.LoyaltyProgramException;
import org.mq.optculture.utils.OCConstants;
import org.springframework.jdbc.core.JdbcTemplate;

public class LoyaltyTransactionParentDaoForDML extends AbstractSpringDaoForDML{

	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);
	
	private SessionFactory sessionFactory;
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public LoyaltyTransactionParentDaoForDML() {
	}
	
	public void saveOrUpdate(LoyaltyTransactionParent loyaltyTransactionParent) {
		super.saveOrUpdate(loyaltyTransactionParent);
	}
	
	/**
	 * It fetches all the transactions of the points activation date between given dates;
	 * @param startTime
	 * @param endTime
	 * @return
	 * @throws LoyaltyProgramException
	 *//*
	public List<LoyaltyProgramTrans> fetchActiveTransOftheHour(Calendar startTime, Calendar endTime) throws LoyaltyProgramException{
		List<LoyaltyProgramTrans> transList = null;
		try{
			
			String queryStr = " FROM LoyaltyProgramTrans WHERE netEarnedValueStatus = 'InActive' "
					+ " AND type IN ('"+OCConstants.LOYALTY_ENROLLMENT+"','"+OCConstants.LOYALTY_ISSUANCE+"','"+OCConstants.LOYALTY_ADJUSTMENT+"')"     
					+ " ptsActivationDate BETWEEN "+startTime+" AND "+endTime;
			
			transList  = (List<LoyaltyProgramTrans>)getHibernateTemplate().find(queryStr);
			
		}catch(Exception e){
			logger.error(">>> Exception in fetchActiveTransOftheHour dao >>>");
			throw new LoyaltyProgramException("Fetch transactions failed.");
		}
		return transList;
	}
	
	public List<LoyaltyProgramTrans> fetchCurrentInActiveTrans(Calendar cal, String status, String transType){
		
		List<LoyaltyProgramTrans> transList = null;
		try{
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String date = format.format(cal.getTime());
			
			String queryStr = " FROM LoyaltyProgramTrans WHERE netEarnedValueStatus = 'InActive' "
					+ " AND type IN ('"+OCConstants.LOYALTY_ENROLLMENT+"','"+OCConstants.LOYALTY_ISSUANCE+"','"+OCConstants.LOYALTY_ADJUSTMENT+"')"     
					+ " AND valueActivationDate <= '"+date+"' AND status = 'New'";
			
			String queryStr = " FROM LoyaltyProgramTrans WHERE netEarnedValueStatus = 'InActive' "+ " AND type IN ("+transType+")"     
					+ " AND valueActivationDate <= '"+date+"' AND status = '"+status+"'";
			
			transList  = (List<LoyaltyProgramTrans>)getHibernateTemplate().find(queryStr);
			
		}catch(Exception e){
			logger.error(">>> Exception in fetchCurrentInActiveTrans dao >>>", e);
			//throw new LoyaltyProgramException("Fetch transactions failed.");
		}
		return transList;
		
	}
	
	public List<LoyaltyProgramTrans> fetchCurrentActiveTransForExpiry(Calendar cal){
		
		List<LoyaltyProgramTrans> transList = null;
		try{
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-mm HH:mm::ss");
			String date = format.format(cal.getTime());
			
			String queryStr = " FROM LoyaltyProgramTrans WHERE netEarnedValueStatus = 'Active' "
					+ " AND type = '"+OCConstants.LOYALTY_TRANS_TYPE_ISSUANCE+"'"     
					+ " AND valueExpirationDate <= '"+date+"' AND expiryStatus = 'New'";
			
			transList  = (List<LoyaltyProgramTrans>)getHibernateTemplate().find(queryStr);
			
		}catch(Exception e){
			logger.error(">>> Exception in fetchCurrentActiveTransForExpiry dao >>>");
		}
		return transList;
		
	}
	*/
}
