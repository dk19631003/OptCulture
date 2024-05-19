package org.mq.captiway.scheduler.dao;

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.SessionFactory;
import org.mq.captiway.scheduler.dao.AbstractSpringDao;
import org.mq.captiway.scheduler.utility.Constants;
import org.mq.captiway.scheduler.beans.LoyaltyBalance;
import org.springframework.jdbc.core.JdbcTemplate;

public class LoyaltyBalanceDao extends AbstractSpringDao  {
	 
		private static final Logger logger = LogManager.getLogger(Constants.SCHEDULER_LOGGER);
		public LoyaltyBalanceDao() {}

		private SessionFactory sessionFactory;
		
		private JdbcTemplate jdbcTemplate;

		public JdbcTemplate getJdbcTemplate() {
			return jdbcTemplate;
		}

		public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
			this.jdbcTemplate = jdbcTemplate;
		}
		
		public List<LoyaltyBalance> findBalanceByValueCode(Long userId,String ValueCode){
			
			try {
				List<LoyaltyBalance> list = null;
				list = executeQuery("from LoyaltyBalance where userId='"+userId+"' and valueCode ='"+ValueCode+"'");
				return list;
				
			}
			catch(Exception e) {
				logger.error("Exception ::" , e);
				return null;
			}
			
		}
		public LoyaltyBalance findBy(Long userId,Long programId,Long loyaltyId,String valueCode){
		
			try {
				List<LoyaltyBalance> list = null;
				list = executeQuery("from LoyaltyBalance where userId='"+userId+"' and programId="+programId+" and loyaltyId="+loyaltyId+" and valueCode ='"+valueCode+"' ");
				if(list!=null && !list.isEmpty()) return list.get(0);
				else return null;
			}
			catch(Exception e) {
				logger.error("Exception ::" , e);
				return null;
			}
		
			
			
		}
		public int findLoyaltyBalanceByLoyaltyId(Long loyaltyId){
			
			try {
				String query ="SELECT SUM(balance) FROM LoyaltyBalance WHERE loyaltyId="+loyaltyId+"  GROUP BY loyaltyId";
				
				List<Long> retList = executeQuery(query);
				if(retList != null && !retList.isEmpty()) return retList.get(0).intValue();
				else return 0;
				
			}
			catch(Exception e) {
				logger.error("Exception ::" , e);
				return 0;
			}
		
			
			
		}

		public LoyaltyBalance findByLoyaltyIdUserId(Long userId,Long loyaltyId, Integer requiredpoints, String valueCode){
			
			try {
				List<LoyaltyBalance> list = null;
				list = executeQuery("FROM LoyaltyBalance where userId='"+userId+"' AND loyaltyId="+loyaltyId +" AND valueCode='"+valueCode+"' AND balance>="+requiredpoints);
				return list!= null && !list.isEmpty() ? list.get(0) : null;
				
			}
			catch(Exception e) {
				logger.error("Exception ::" , e);
				return null;
			}
		
			
			
		}	
		
		public LoyaltyBalance findByLoyaltyIdUserIdValueCode(Long userId,Long loyaltyId, String valueCode){
			
			try {
				List<LoyaltyBalance> list = null;
				list = executeQuery("FROM LoyaltyBalance where userId='"+userId+"' AND loyaltyId="+loyaltyId +" AND valueCode='"+valueCode+"' ");
				return list!= null && !list.isEmpty() ? list.get(0) : null;
				
			}
			catch(Exception e) {
				logger.error("Exception ::" , e);
				return null;
			}
		
			
			
		}	
		
		 
		
		public List<LoyaltyBalance> findbyCradNo(String cardNo,Long userId){
			
			try {
				List<LoyaltyBalance> list = null;
				list = executeQuery("from LoyaltyBalance where userId='"+userId+"' and memberShipNumber ='"+cardNo+"'");
				return list;
				
			}
			catch(Exception e) {
				logger.error("Exception ::" , e);
				return null;
			}
			
			
		}
		
		


}
