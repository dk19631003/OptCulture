package org.mq.marketer.campaign.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.hibernate.SessionFactory;
import org.mq.marketer.campaign.beans.LoyaltyBalance;
import org.mq.optculture.utils.OCConstants;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;

public class LoyaltyBalanceDao extends AbstractSpringDao { 

	public LoyaltyBalanceDao() {}

	private SessionFactory sessionFactory;
	
	private JdbcTemplate jdbcTemplate;

	public JdbcTemplate getJdbcTemplate() {
		return jdbcTemplate;
	}

	public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}
	
	public List<LoyaltyBalance> findBalanceByValueCode(Long userId,String ValueCode,String cardNumber){
		
		try {
			List<LoyaltyBalance> list = null;
			list = executeQuery("from LoyaltyBalance where userId='"+userId+"' and valueCode ='"+ValueCode+"' and memberShipNumber='"+cardNumber+"'");
			return list;
			
		}
		catch(Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
		
	}
	public List<LoyaltyBalance> findAllBy(Long userId,Long programId,Long loyaltyId,String valueCode){
		
		try {
			List<LoyaltyBalance> list = null;
			list = executeQuery("from LoyaltyBalance where userId='"+userId+"' and "
					+ "programId="+programId+" and loyaltyId="+loyaltyId+" and valueCode IN("+valueCode+") ");
			if(list!=null && !list.isEmpty()) return list;
			else return null;
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
	
	public List<LoyaltyBalance> findBy(Long userID, Long loyaltyID) {
		try {
		List<LoyaltyBalance> list = null;
		list = executeQuery("FROM LoyaltyBalance WHERE userId='"+userID+"' and loyaltyId ="+loyaltyID+" "
		+ "AND valueCode IN(SELECT rewardValueCode from SpecialReward where createdBy="+userID+""
		+ " AND statusSpecialReward='Active' AND associatedWithFBP=false)");
		return list;

		}
		catch(Exception e) {
		logger.error("Exception ::" , e);
		return null;
		}

		}
	
	public List<LoyaltyBalance> findOnlyRewardsBy(Long userID, Long loyaltyID, Long orgID) {
		try {
			List<LoyaltyBalance> list = null;
			list = executeQuery("FROM LoyaltyBalance WHERE userId='"+userID+"' and loyaltyId ="+loyaltyID+" AND valueCode "
					+ "IN(SELECT ValuCode from ValueCodes WHERE OrgId="+orgID+" AND associatedWithFBP=false)");
			return list;
			
		}
		catch(Exception e) {
			logger.error("Exception ::" , e);
			return null;
		}
		
	}
	
	public LoyaltyBalance findBy(Long userID, Long loyaltyID, String valueCode) {
		try {
		List<LoyaltyBalance> list = null;
		list = executeQuery("FROM LoyaltyBalance WHERE userId='"+userID+"' "
		+ "and loyaltyId ="+loyaltyID+" AND valueCode='"+valueCode+"' ");
		if(list != null && !list.isEmpty()) return list.get(0);
		else return null;

		}
		catch(Exception e) {
		logger.error("Exception ::" , e);
		return null;
		}
		}
	public List<Object[]> getLiabilityByValueCode(String valueCode,Long userId){
		String query="SELECT sum(balance) as liability,sum(total_earn_balance) as issued from loyalty_balance "
				+ "where user_id="+userId+" AND value_code='"+valueCode+"' ";
		List<Object[]> list= jdbcTemplate.query(query, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 Object obj[]=new Object[2];
				 obj[0]=rs.getLong("issued");
				 obj[1]=rs.getLong("liability");
				 return obj;
			 }
		 } );
		return list;
				
	}
	
	public List<Object[]> getLiabilityListByValueCode(String valueCode,Long userId,String prog){
		String query="SELECT program_id,sum(balance) as liability,sum(total_earn_balance) as issued from loyalty_balance "
				+ "where user_id="+userId+" AND program_id="+prog+" AND value_code='"+valueCode+"'"; 
						//+ "//GROUP BY program_id";
		List<Object[]> list= jdbcTemplate.query(query, new RowMapper() {
			 public Object mapRow(ResultSet rs, int rowNum) throws SQLException {
				 Object obj[]=new Object[3];
				 obj[0]=rs.getLong("issued");
				 obj[1]=rs.getLong("liability");
				 obj[2]=rs.getLong("program_id");
				 return obj;
			 }
		 } );
		return list;
				
	}
	public int findLoyaltyBalanceByLoyaltyId(Long loyaltyId, Long userID){

		try {
		String query ="SELECT SUM(balance) FROM LoyaltyBalance WHERE loyaltyId="+loyaltyId+ " "
		+ "AND valueCode IN(SELECT rewardValueCode from SpecialReward where createdBy="+userID+""
		+ " AND statusSpecialReward='Active' AND associatedWithFBP=false) GROUP BY loyaltyId";

		List<Long> retList = executeQuery(query);
		if(retList != null && !retList.isEmpty()) return retList.get(0).intValue();
		else return 0;

		}
		catch(Exception e) {
		logger.error("Exception ::" , e);
		return 0;
		}



		}
}
