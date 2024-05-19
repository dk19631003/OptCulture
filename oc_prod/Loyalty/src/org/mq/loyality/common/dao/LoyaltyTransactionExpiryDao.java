package org.mq.loyality.common.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.loyality.utils.Constants;
import org.springframework.beans.factory.annotation.Autowired;
public class LoyaltyTransactionExpiryDao {
	
	@Autowired
	private SessionFactory sessionFactory;
	private static final Logger logger = LogManager.getLogger(Constants.LOYALTY_LOGGER);
	public LoyaltyTransactionExpiryDao() {
	}
	@SuppressWarnings("unchecked")
	public  Object[] fetchExpiryValues(Long loyaltyId, String expDate, String rewardFlag){
		List<Object[]> expireList = null;
		try{
		String queryStr = " SELECT membership_number, SUM(expiry_points) as aggExpPoints, SUM(expiry_amount) as aggExpAmt "
				+ " FROM loyalty_transaction_expiry WHERE loyalty_id = "+loyaltyId
				+ " AND reward_flag = '"+rewardFlag+"'"
				+ " AND (expiry_points > 0 OR expiry_amount > 0)"
				+ " AND STR_TO_DATE(CONCAT(YEAR(created_date),'-', MONTH(created_date)),'%Y-%m') <= STR_TO_DATE('"+expDate+"', '%Y-%m')";
		expireList=(List<Object[]>) sessionFactory.getCurrentSession().createSQLQuery(queryStr).list();
		
		if(expireList != null && expireList.size() > 0 && expireList.get(0) != null){
			return expireList.get(0);
		}
		else return null;
		}catch(Exception e){
			logger.info(" Exception :: ",e);
		}
		return null;
	}
	
	
	
	
	

}
