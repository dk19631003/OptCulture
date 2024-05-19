package org.mq.marketer.campaign.dao;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.AsyncLoyaltyTrx;
import org.mq.marketer.campaign.general.Constants;
import org.springframework.jdbc.core.JdbcTemplate;

public class AsyncLoyaltyTrxDaoForDML extends AbstractSpringDaoForDML {
	
	
	private static final Logger logger = LogManager.getLogger(Constants.SUBSCRIBER_LOGGER);

	//private SessionFactory sessionFactory;
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	public AsyncLoyaltyTrxDaoForDML() {
	}
	
	/*public AsyncLoyaltyTrx find(Long id) {
		return (AsyncLoyaltyTrx) super.find(AsyncLoyaltyTrx.class, id);
	}*/

	public void saveOrUpdate(AsyncLoyaltyTrx asyncLoyaltyTrx) {
		super.saveOrUpdate(asyncLoyaltyTrx);
	}
	public void merge(AsyncLoyaltyTrx asyncLoyaltyTrx) {
		super.merge(asyncLoyaltyTrx);
	}
	public void delete(AsyncLoyaltyTrx asyncLoyaltyTrx) {
		super.delete(asyncLoyaltyTrx);
	}
	
	/*public List<AsyncLoyaltyTrx> getNewByStatus(String trxType, String status){
		List<AsyncLoyaltyTrx> loyaltyTrxList = new ArrayList<AsyncLoyaltyTrx>();
		try{
		loyaltyTrxList = executeQuery(" FROM AsyncLoyaltyTrx WHERE status='"+status+"' AND trxType = '"+trxType+"'");
		}catch(Exception e){
			logger.info(" Exception while getting list of transactions :: ",e);
		}
		return loyaltyTrxList;
	}
*/
}
